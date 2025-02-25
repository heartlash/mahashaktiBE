package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.constants.EggType;
import com.mahashakti.mahashaktiBE.entities.ProductionEntity;
import com.mahashakti.mahashaktiBE.exception.InvalidDataStateException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.ProductionRepository;
import com.mahashakti.mahashaktiBe.model.Production;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final FlockService flockService;
    private final AnalyticsService analyticsService;
    private final MaterialConsumptionService materialConsumptionService;

    public List<ProductionEntity> getAllProduction(Date startDate, Date endDate) {
        List<ProductionEntity> productionEntityList = productionRepository
                .findByProductionDateBetweenOrderByProductionDateDesc(startDate, endDate)
                .stream().peek(productionEntity -> {
                    productionEntity.setSaleableGradeACount(getSaleableGradeAEggCount(productionEntity));
                    productionEntity.setSaleableGradeBCount(getSaleableGradeBEggCount(productionEntity));
                }).toList();
        return productionEntityList;
    }

    public List<ProductionEntity> getAllProductionShedId(Date startDate, Date endDate, Integer shedId) {
        List<ProductionEntity> productionEntityList = productionRepository
                .findByProductionDateBetweenAndShedIdOrderByProductionDateDesc(startDate, endDate, shedId)
                .stream().peek(productionEntity -> {
            productionEntity.setSaleableGradeACount(getSaleableGradeAEggCount(productionEntity));
            productionEntity.setSaleableGradeBCount(getSaleableGradeBEggCount(productionEntity));
        }).toList();
        return productionEntityList;
    }

    public ProductionEntity postProduction(Production production) {
        Optional<ProductionEntity> productionEntityOptional = productionRepository.findByProductionDateAndShedId(
                production.getProductionDate(), production.getShedId());
        if(productionEntityOptional.isPresent()) throw new InvalidDataStateException("Production Data Already Present");
        ProductionEntity productionEntity = new ProductionEntity();

        BeanUtils.copyProperties(production, productionEntity);

        int saleableGradeACount = getSaleableGradeAEggCount(productionEntity);

        int saleableGradeBCount = getSaleableGradeBEggCount(productionEntity);

        if(saleableGradeACount < 0 || saleableGradeBCount < 0) throw new InvalidDataStateException("Saleable counts  invalid");
        
        productionEntity.setSaleableGradeACount(saleableGradeACount);
        productionEntity.setSaleableGradeBCount(saleableGradeBCount);

        productionEntity.setProductionPercentage(calculateProductionPercentage(production.getProducedCount(), production.getShedId()));

        ProductionEntity productionEntitySaved = productionRepository.save(productionEntity);

        analyticsService.incrementEggStockCount(saleableGradeACount, EggType.GRADE_A);
        analyticsService.incrementEggStockCount(saleableGradeBCount, EggType.GRADE_B);


        Map<Integer, Integer> shedToFlock = Collections.singletonMap(
                production.getShedId(),
                flockService.getFlockShedCount(production.getShedId()).getCount());

        materialConsumptionService.postDailyMaterialConsumption(shedToFlock, production.getProductionDate());

        return productionEntitySaved;
    }

    public ProductionEntity getProductionById(UUID productionId) {
        Optional<ProductionEntity> productionEntityOptional = productionRepository.findById(productionId);
        if(productionEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Production Resource Not Found %s", productionId.toString()));

        return productionEntityOptional.get();
    }

    public List<ProductionEntity> getProductionLatest() {
        Optional<ProductionEntity> productionEntityOptionalLatest = productionRepository.findTopByOrderByProductionDateDesc();
        if(productionEntityOptionalLatest.isEmpty())
            throw new ResourceNotFoundException("Latest Production Resource Not Found %s");
        Date productionDate = productionEntityOptionalLatest.get().getProductionDate();

        return productionRepository.findByProductionDate(productionDate);
    }

    public ProductionEntity putProductionById(UUID productionId, Production production) {

        if(!productionId.equals(production.getId()))
            throw new MismatchException("Production Resource ID Mismatch in Put Request");

        ProductionEntity productionEntityInDb = getProductionById(productionId);

        analyticsService.decrementEggStockCount(getSaleableGradeAEggCount(productionEntityInDb), EggType.GRADE_A);
        analyticsService.decrementEggStockCount(getSaleableGradeBEggCount(productionEntityInDb), EggType.GRADE_B);

        BeanUtils.copyProperties(production, productionEntityInDb, "createdBy", "createdAt");

        int saleableGradeACount = production.getProducedCount() - production.getBrokenCount() - production.getSelfUseCount()
                - production.getGiftCount();

        int saleableGradeBCount = production.getBrokenCount() - production.getWasteCount();

        if(saleableGradeACount < 0 || saleableGradeBCount < 0) throw new InvalidDataStateException("Saleable count is invalid");
        
        productionEntityInDb.setSaleableGradeACount(saleableGradeACount);
        productionEntityInDb.setSaleableGradeBCount(saleableGradeBCount);

        productionEntityInDb.setProductionPercentage(calculateProductionPercentage(production.getProducedCount(), production.getShedId()));

        analyticsService.incrementEggStockCount(productionEntityInDb.getSaleableGradeACount(), EggType.GRADE_A);
        analyticsService.incrementEggStockCount(productionEntityInDb.getSaleableGradeBCount(), EggType.GRADE_B);


        return productionRepository.save(productionEntityInDb);
    }

    public void deleteProductionById(UUID productionId) {
        ProductionEntity productionEntity = getProductionById(productionId);

        analyticsService.decrementEggStockCount(getSaleableGradeAEggCount(productionEntity), EggType.GRADE_A);
        analyticsService.decrementEggStockCount(getSaleableGradeBEggCount(productionEntity), EggType.GRADE_B);

        productionRepository.deleteById(productionId);
        materialConsumptionService.deleteMaterialConsumptionByConsumptionDate(productionEntity.getShedId(), productionEntity.getProductionDate());
    }

    private BigDecimal calculateProductionPercentage(Integer producedCount, Integer shedId) {
        BigDecimal producedCountDecimal = new BigDecimal(producedCount);
        BigDecimal flockCountDecimal = new BigDecimal(flockService.getFlockCountMap().get(shedId));

        BigDecimal result = producedCountDecimal.divide(flockCountDecimal, 4, RoundingMode.DOWN);
        return result.multiply(new BigDecimal(100)).setScale(2, RoundingMode.DOWN);
    }

    public Integer getSaleableGradeAEggCount(ProductionEntity productionEntity) {
        return productionEntity.getProducedCount() - productionEntity.getBrokenCount() - productionEntity.getSelfUseCount()
                - productionEntity.getGiftCount();

    }

    public Integer getSaleableGradeBEggCount(ProductionEntity productionEntity) {
        return productionEntity.getBrokenCount() - productionEntity.getWasteCount();

    }

}
