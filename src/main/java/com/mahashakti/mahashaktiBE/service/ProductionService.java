package com.mahashakti.mahashaktiBE.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final FlockService flockService;
    private final AnalyticsService analyticsService;

    public List<ProductionEntity> getAllProduction(Date startDate, Date endDate) {
        return productionRepository.findByProductionDateBetweenOrderByProductionDateAsc(startDate, endDate);
    }

    public ProductionEntity postProduction(Production production) {
        Optional<ProductionEntity> productionEntityOptional = productionRepository.findByProductionDate(production.getProductionDate());
        if(productionEntityOptional.isPresent()) throw new InvalidDataStateException("Production Data Already Present");
        ProductionEntity productionEntity = new ProductionEntity();

        BeanUtils.copyProperties(production, productionEntity);

        int saleableCount = production.getProducedCount() - production.getBrokenCount() - production.getSelfUseCount()
                - production.getGiftCount();

        if(saleableCount < 0) throw new InvalidDataStateException(String.format("Saleable count: %d is invalid", saleableCount));
        productionEntity.setSaleableCount(saleableCount);

        productionEntity.setProductionPercentage(calculateProductionPercentage(production.getProducedCount()));

        ProductionEntity productionEntitySaved = productionRepository.save(productionEntity);
        analyticsService.incrementEggStockCount(saleableCount);
        return productionEntitySaved;
    }

    public ProductionEntity getProductionById(UUID productionId) {
        Optional<ProductionEntity> productionEntityOptional = productionRepository.findById(productionId);
        if(productionEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Production Resource Not Found %s", productionId.toString()));

        return productionEntityOptional.get();
    }

    public ProductionEntity getProductionLatest() {
        Optional<ProductionEntity> productionEntityOptionalLatest = productionRepository.findTopByOrderByProductionDateDesc();
        if(productionEntityOptionalLatest.isEmpty())
            throw new ResourceNotFoundException("Latest Production Resource Not Found %s");

        return productionEntityOptionalLatest.get();
    }

    public ProductionEntity putProductionById(UUID productionId, Production production) {

        if(!productionId.equals(production.getId()))
            throw new MismatchException("Production Resource ID Mismatch in Put Request");

        ProductionEntity productionEntityInDb = getProductionById(productionId);
        analyticsService.decrementEggStockCount(productionEntityInDb.getSaleableCount());

        BeanUtils.copyProperties(production, productionEntityInDb, "createdBy", "createdAt");

        int saleableCount = production.getProducedCount() - production.getBrokenCount() - production.getSelfUseCount()
                - production.getGiftCount();

        if(saleableCount < 0) throw new InvalidDataStateException(String.format("Saleable count: %d is invalid", saleableCount));
        productionEntityInDb.setSaleableCount(saleableCount);

        productionEntityInDb.setProductionPercentage(calculateProductionPercentage(production.getProducedCount()));
        analyticsService.incrementEggStockCount(productionEntityInDb.getSaleableCount());

        return productionRepository.save(productionEntityInDb);
    }

    public void deleteProductionById(UUID productionId) {
        ProductionEntity productionEntity = getProductionById(productionId);
        analyticsService.decrementEggStockCount(productionEntity.getSaleableCount());
        productionRepository.deleteById(productionId);
    }

    private BigDecimal calculateProductionPercentage(Integer producedCount) {
        BigDecimal producedCountDecimal = new BigDecimal(producedCount);
        BigDecimal flockCountDecimal = new BigDecimal(flockService.getFlockCount().getCount());

        BigDecimal result = producedCountDecimal.divide(flockCountDecimal, 4, RoundingMode.DOWN);
        return result.multiply(new BigDecimal(100)).setScale(2, RoundingMode.DOWN);
    }

}