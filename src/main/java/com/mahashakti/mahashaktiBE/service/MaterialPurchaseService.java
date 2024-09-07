package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.exception.MahashaktiException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialPurchaseRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
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
import java.util.ArrayList;




import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialPurchaseService {


    private final MaterialPurchaseRepository materialPurchaseRepository;
    private final DataService dataService;
    private final MaterialStockRepository materialStockRepository;


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

        List<MaterialStockEntity> materialStockEntityList = new ArrayList<>();

        List<MaterialPurchaseEntity> materialPurchaseEntityList =  materialPurchases.stream()
                .map(materialPurchase -> {
                    MaterialPurchaseEntity materialPurchaseEntity = new MaterialPurchaseEntity();
                    try {
                        BeanUtils.copyProperties(materialPurchase, materialPurchaseEntity);
                        MaterialEntity material = dataService.getMaterialById(materialPurchase.getMaterialId());
                        materialPurchaseEntity.setMaterial(material);
                        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialPurchase.getMaterialId());
                        materialStockEntity.setQuantity(materialStockEntity.getQuantity().add(materialPurchase.getQuantity()));
                        materialStockEntity.setLastPurchaseDate(new Date());
                        materialStockEntity.setLastPurchaseRate(materialPurchase.getRate());
                        materialStockEntity.setMaterial(material);
                        materialStockEntityList.add(materialStockEntity);
                    } catch (Exception e) {
                        throw new MahashaktiException();
                    }
                    return materialPurchaseEntity;
                })
                .collect(Collectors.toList());

        materialStockRepository.saveAll(materialStockEntityList);

        return materialPurchaseRepository.saveAll(materialPurchaseEntityList);

    }

    @Transactional
    public MaterialPurchaseEntity putMaterialPurchaseById(UUID materialPurchaseId, MaterialPurchase materialPurchase) {
        if(!materialPurchaseId.equals(materialPurchase.getId()))
            throw new MismatchException("Material Purchase Expense ID Mismatch in Put Request");

        MaterialPurchaseEntity materialPurchaseEntityInDb = getMaterialPurchaseById(materialPurchaseId);

        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialPurchase.getMaterialId());
        if(!materialPurchaseEntityInDb.getQuantity().equals(materialPurchase.getQuantity())) {

            materialStockEntity.setQuantity(materialStockEntity.getQuantity()
                    .subtract(materialPurchaseEntityInDb.getQuantity())
                    .add(materialPurchase.getQuantity()));
        }

        BeanUtils.copyProperties(materialPurchase, materialPurchaseEntityInDb, "createdBy", "createdAt");

        if(!materialPurchaseEntityInDb.getMaterial().getId().equals(materialPurchase.getMaterialId())) {
            MaterialEntity materialEntity = dataService.getMaterialById(materialPurchase.getMaterialId());
            materialPurchaseEntityInDb.setMaterial(materialEntity);
            materialStockEntity.setMaterial(materialEntity);
        }

        materialStockRepository.save(materialStockEntity);
        return materialPurchaseRepository.save(materialPurchaseEntityInDb);

    }

    @Transactional
    public void deleteMaterialPurchaseById(UUID materialPurchaseId) {
        MaterialPurchaseEntity materialPurchaseEntity = getMaterialPurchaseById(materialPurchaseId);
        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialPurchaseEntity.getMaterial().getId());

        materialStockEntity.setQuantity(materialStockEntity.getQuantity().subtract(materialPurchaseEntity.getQuantity()));
        materialStockRepository.save(materialStockEntity);
        materialPurchaseRepository.deleteById(materialPurchaseId);
    }

}
