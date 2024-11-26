package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialConsumptionEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.exception.InvalidDataStateException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialConsumptionRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
import com.mahashakti.mahashaktiBE.utils.MaterialStockCalculator;
import com.mahashakti.mahashaktiBe.model.MaterialConsumption;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialConsumptionService {

    private final MaterialConsumptionRepository materialConsumptionRepository;
    private final DataService dataService;
    private final MaterialStockRepository materialStockRepository;
    private final MaterialStockCalculator materialStockCalculator;

    public List<MaterialConsumptionEntity> getAllMaterialConsumption(Date startDate, Date endDate) {
        return materialConsumptionRepository.findByConsumptionDateBetweenOrderByConsumptionDateAsc(startDate, endDate);
    }


    @Transactional
    public List<MaterialConsumptionEntity> postMaterialConsumptions(List<MaterialConsumption> materialConsumptions) {

        List<MaterialStockEntity> materialStockEntityList = new ArrayList<>();

        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptions.stream().map(materialConsumption -> {

            MaterialConsumptionEntity materialConsumptionEntity = new MaterialConsumptionEntity();
            BeanUtils.copyProperties(materialConsumption, materialConsumptionEntity);

            materialConsumptionEntity.setMaterial(dataService.getMaterialById(materialConsumption.getMaterialId()));
            materialConsumptionEntity.setShed(dataService.getShedById(materialConsumption.getShedId()));

            MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialConsumption.getMaterialId());

            if(materialStockEntity.getQuantity().compareTo(new BigDecimal(0)) == 0) return null;

            BigDecimal stockQuantity = materialStockEntity.getQuantity().subtract(materialConsumption.getQuantity());

            if(stockQuantity.compareTo(BigDecimal.ZERO) < 0) {
                materialConsumptionEntity.setQuantity(materialStockEntity.getQuantity());
                materialStockEntity.setQuantity(new BigDecimal(0));
            }
            else
                materialStockEntity.setQuantity(stockQuantity);
            materialStockEntityList.add(materialStockEntity);

            return materialConsumptionEntity;
        }).filter(Objects::nonNull).toList();

        materialStockRepository.saveAll(materialStockEntityList);
        return materialConsumptionRepository.saveAll(materialConsumptionEntityList);

    }


    public MaterialConsumptionEntity getMaterialConsumptionById(UUID consumptionId) {
        Optional<MaterialConsumptionEntity> materialConsumptionEntityOptional = materialConsumptionRepository.findById(consumptionId);
        if(materialConsumptionEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Consumption Resource Not Found %s", consumptionId.toString()));

        return materialConsumptionEntityOptional.get();
    }

    public List<MaterialConsumptionEntity> getMaterialConsumptionByMaterialId(Integer materialId, Date startDate, Date endDate) {
        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptionRepository.findByConsumptionDateBetweenAndMaterialId(startDate, endDate, materialId);
        if(materialConsumptionEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Consumption Resource Not Found with materialId %d", materialId));

        return materialConsumptionEntityList;
    }

    @Transactional
    public MaterialConsumptionEntity putMaterialConsumption(UUID consumptionId, MaterialConsumption materialConsumption) {
        if(!consumptionId.equals(materialConsumption.getId()))
            throw new MismatchException("Material Consumption Resource ID Mismatch in Put Request");

        MaterialConsumptionEntity materialConsumptionEntityInDb = getMaterialConsumptionById(consumptionId);

        if(!materialConsumptionEntityInDb.getQuantity().equals(materialConsumption.getQuantity())) {
            MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialConsumption.getMaterialId());

            BigDecimal stockQuantity = materialStockEntity.getQuantity()
                    .add(materialConsumptionEntityInDb.getQuantity())
                    .subtract(materialConsumption.getQuantity());

            if(stockQuantity.compareTo(BigDecimal.ZERO) < 0) throw new InvalidDataStateException(
                    "Final Stock Value Cannot Be Negative:"
            );
            materialStockEntity.setQuantity(stockQuantity);
            materialStockRepository.save(materialStockEntity);
        }

        BeanUtils.copyProperties(materialConsumption, materialConsumptionEntityInDb, "createdBy", "createdAt");

        if(!materialConsumptionEntityInDb.getMaterial().getId().equals(materialConsumption.getMaterialId()))
            materialConsumptionEntityInDb.setMaterial(dataService.getMaterialById(materialConsumption.getMaterialId()));

        return materialConsumptionRepository.save(materialConsumptionEntityInDb);
    }

    @Transactional
    public void deleteMaterialConsumptionById(UUID consumptionId) {
        MaterialConsumptionEntity materialConsumptionEntity = getMaterialConsumptionById(consumptionId);
        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialConsumptionEntity.getMaterial().getId());

        materialStockEntity.setQuantity(materialStockEntity.getQuantity().add(materialConsumptionEntity.getQuantity()));
        materialStockRepository.save(materialStockEntity);
        materialConsumptionRepository.deleteById(consumptionId);
    }

    public List<MaterialConsumptionEntity> postDailyMaterialConsumption(Map<Integer, Integer> flockToShed, Date productionDate) {

        List<MaterialConsumption> materialConsumptionList = dataService.getMaterials().stream().map(materialEntity -> {
            MaterialConsumption materialConsumption = new MaterialConsumption();

            materialConsumption.setConsumptionDate(productionDate);
            materialConsumption.setMaterialId(materialEntity.getId());
            materialConsumption.setQuantity(materialStockCalculator
                    .getDailyExpectedMaterialConsumption(materialEntity.getName(), flockToShed));
            materialConsumption.setShedId(flockToShed.keySet().stream().toList().getFirst());
            materialConsumption.setCreatedAt(new Date());
            materialConsumption.setCreatedBy("Mahashakti System");

            return materialConsumption;
        }).toList();

        return postMaterialConsumptions(materialConsumptionList);
    }

    public void deleteMaterialConsumptionByConsumptionDate(Integer shedId, Date consumptionDate) {

        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptionRepository.getByConsumptionDateAndShedId(consumptionDate, shedId);
        if(materialConsumptionEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Resource Not Found for shed %d production date %s", shedId, consumptionDate));
        materialConsumptionEntityList.forEach(materialConsumptionEntity -> {
            deleteMaterialConsumptionById(materialConsumptionEntity.getId());
        });

    }

}
