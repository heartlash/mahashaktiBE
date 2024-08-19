package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialConsumptionEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.exception.InvalidDataStateException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialConsumptionRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
import com.mahashakti.mahashaktiBe.model.MaterialConsumption;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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

    public List<MaterialConsumptionEntity> getAllMaterialConsumption(Date startDate, Date endDate) {
        return materialConsumptionRepository.findByConsumptionDateBetween(startDate, endDate);
    }


    @Transactional
    public List<MaterialConsumptionEntity> postMaterialConsumptions(List<MaterialConsumption> materialConsumptions) {

        List<MaterialStockEntity> materialStockEntityList = new ArrayList<>();

        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptions.stream().map(materialConsumption -> {

            MaterialConsumptionEntity materialConsumptionEntity = new MaterialConsumptionEntity();
            BeanUtils.copyProperties(materialConsumption, materialConsumptionEntity);

            materialConsumptionEntity.setMaterial(dataService.getMaterialById(materialConsumption.getMaterialId()));

            MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialConsumption.getMaterialId());
            BigDecimal stockQuantity = materialStockEntity.getQuantity().subtract(materialConsumption.getQuantity());
            if(stockQuantity.compareTo(BigDecimal.ZERO) < 0) throw new InvalidDataStateException(
                    "Final Stock Value Cannot Be Negative:"
            );
            materialStockEntity.setQuantity(stockQuantity);
            materialStockEntityList.add(materialStockEntity);

            return materialConsumptionEntity;
        }).toList();

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
                    .subtract(materialConsumptionEntityInDb.getQuantity())
                    .add(materialConsumption.getQuantity());

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

        materialStockEntity.setQuantity(materialStockEntity.getQuantity().subtract(materialConsumptionEntity.getQuantity()));
        materialStockRepository.save(materialStockEntity);
        materialConsumptionRepository.deleteById(consumptionId);
    }
}
