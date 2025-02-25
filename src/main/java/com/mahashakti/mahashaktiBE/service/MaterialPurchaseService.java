package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.exception.MahashaktiException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialPurchaseRepository;
import com.mahashakti.mahashaktiBe.model.LatestMaterialPurchase;
import com.mahashakti.mahashaktiBe.model.MaterialPurchase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialPurchaseService {


    private final MaterialPurchaseRepository materialPurchaseRepository;
    private final DataService dataService;

    public List<MaterialPurchaseEntity> getAllMaterialPurchases(Date startDate, Date endDate, String createdBy) {
        if(Objects.isNull(createdBy)) return materialPurchaseRepository.findByPurchaseDateBetweenOrderByPurchaseDateAsc(startDate, endDate);
        else return materialPurchaseRepository.findByPurchaseDateBetweenAndCreatedBy(startDate, endDate, createdBy);
    }

    public MaterialPurchaseEntity getMaterialPurchaseById(UUID materialPurchaseId) {
        Optional<MaterialPurchaseEntity> materialPurchaseEntityOptional = materialPurchaseRepository.findById(materialPurchaseId);
        if(materialPurchaseEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Expense Resource Not Found: %s", materialPurchaseId.toString()));

        return materialPurchaseEntityOptional.get();
    }

    public List<MaterialPurchaseEntity> getMaterialPurchaseByMaterialId(Integer materialId, Date startDate, Date endDate) {
        List<MaterialPurchaseEntity> materialPurchaseEntityList = materialPurchaseRepository.findByPurchaseDateBetweenAndMaterialId(startDate, endDate, materialId);
        if(materialPurchaseEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Expense Resource Not Found with Material Id: %d", materialId));

        return materialPurchaseEntityList;
    }


    @Transactional
    public List<MaterialPurchaseEntity> postMaterialPurchases(List<MaterialPurchase> materialPurchases) {

        List<MaterialPurchaseEntity> materialPurchaseEntityList =  materialPurchases.stream()
                .map(materialPurchase -> {
                    MaterialPurchaseEntity materialPurchaseEntity = new MaterialPurchaseEntity();
                    try {
                        BeanUtils.copyProperties(materialPurchase, materialPurchaseEntity);
                        MaterialEntity material = dataService.getMaterialById(materialPurchase.getMaterialId());
                        materialPurchaseEntity.setMaterial(material);
                    } catch (Exception e) {
                        throw new MahashaktiException();
                    }
                    return materialPurchaseEntity;
                })
                .collect(Collectors.toList());

        return materialPurchaseRepository.saveAll(materialPurchaseEntityList);

    }

    @Transactional
    public MaterialPurchaseEntity putMaterialPurchaseById(UUID materialPurchaseId, MaterialPurchase materialPurchase) {
        if(!materialPurchaseId.equals(materialPurchase.getId()))
            throw new MismatchException("Material Purchase Expense ID Mismatch in Put Request");

        MaterialPurchaseEntity materialPurchaseEntityInDb = getMaterialPurchaseById(materialPurchaseId);

        BeanUtils.copyProperties(materialPurchase, materialPurchaseEntityInDb, "createdBy", "createdAt");

        if(!materialPurchaseEntityInDb.getMaterial().getId().equals(materialPurchase.getMaterialId())) {
            MaterialEntity materialEntity = dataService.getMaterialById(materialPurchase.getMaterialId());
            materialPurchaseEntityInDb.setMaterial(materialEntity);
        }

        return materialPurchaseRepository.save(materialPurchaseEntityInDb);

    }

    @Transactional
    public void deleteMaterialPurchaseById(UUID materialPurchaseId) {
        MaterialPurchaseEntity materialPurchaseEntity = getMaterialPurchaseById(materialPurchaseId);
        materialPurchaseRepository.deleteById(materialPurchaseId);
    }

    public Map<Integer, LatestMaterialPurchase> getLatestMaterialPurchase() {
        List<MaterialEntity> materialEntityList = dataService.getMaterials();
        Map<Integer, LatestMaterialPurchase> materialToLatestPurchase = new HashMap<>();
        materialEntityList.forEach(materialEntity -> {
            Optional<MaterialPurchaseEntity> optionalMaterialPurchaseEntity = materialPurchaseRepository
                    .findTopByMaterialIdOrderByPurchaseDateDesc(materialEntity.getId());
            if(optionalMaterialPurchaseEntity.isPresent()) {
                LatestMaterialPurchase latestMaterialPurchase = new LatestMaterialPurchase();
                BeanUtils.copyProperties(optionalMaterialPurchaseEntity.get(), latestMaterialPurchase);
                materialToLatestPurchase.put(materialEntity.getId(), latestMaterialPurchase);
            }
        });

        return materialToLatestPurchase;

    }
}
