package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialConsumptionEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialConsumptionRepository;
import com.mahashakti.mahashaktiBe.model.MaterialConsumption;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialConsumptionService {

    private final MaterialConsumptionRepository materialConsumptionRepository;
    private final DataService dataService;

    public List<MaterialConsumptionEntity> getAllMaterialConsumption(Date startDate, Date endDate) {
        return materialConsumptionRepository.findByConsumptionDateBetween(startDate, endDate);
    }


    @Transactional
    public List<MaterialConsumptionEntity> postMaterialConsumptions(List<MaterialConsumption> materialConsumptions) {
        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptions.stream().map(materialConsumption -> {
            MaterialConsumptionEntity materialConsumptionEntity = new MaterialConsumptionEntity();
            BeanUtils.copyProperties(materialConsumption, materialConsumptionEntity);
            materialConsumptionEntity.setMaterial(dataService.getMaterialById(materialConsumption.getMaterialId()));
            return materialConsumptionEntity;
        }).toList();

        return materialConsumptionRepository.saveAll(materialConsumptionEntityList);

    }


    public MaterialConsumptionEntity getMaterialConsumptionById(UUID consumptionId) {
        Optional<MaterialConsumptionEntity> materialConsumptionEntityOptional = materialConsumptionRepository.findById(consumptionId);
        if(materialConsumptionEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Consumption Resource Not Found %s", consumptionId.toString()));

        return materialConsumptionEntityOptional.get();
    }

    public List<MaterialConsumptionEntity> getMaterialConsumptionByMaterialId(Integer materialId) {
        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptionRepository.findByMaterialId(materialId);
        if(materialConsumptionEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Consumption Resource Not Found with materialId %d", materialId));

        return materialConsumptionEntityList;
    }

    public MaterialConsumptionEntity putMaterialConsumption(UUID consumptionId, MaterialConsumption materialConsumption) {
        if(!consumptionId.equals(materialConsumption.getId()))
            throw new MismatchException("Material Consumption Resource ID Mismatch in Put Request");

        MaterialConsumptionEntity materialConsumptionEntityInDb = getMaterialConsumptionById(consumptionId);

        BeanUtils.copyProperties(materialConsumption, materialConsumptionEntityInDb, "createdBy", "createdAt");

        if(!materialConsumptionEntityInDb.getMaterial().getId().equals(materialConsumption.getMaterialId()))
            materialConsumptionEntityInDb.setMaterial(dataService.getMaterialById(materialConsumption.getMaterialId()));

        return materialConsumptionRepository.save(materialConsumptionEntityInDb);
    }

    public void deleteMaterialConsumptionById(UUID consumptionId) {
        materialConsumptionRepository.deleteById(consumptionId);
    }
}
