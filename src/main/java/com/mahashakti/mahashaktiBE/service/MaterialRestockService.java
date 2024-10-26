package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialRestockEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialRestockRepository;

import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
import com.mahashakti.mahashaktiBe.model.MaterialRestock;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialRestockService {

    private final MaterialRestockRepository materialRestockRepository;
    private final DataService dataService;
    private final MaterialStockRepository materialStockRepository;

    public List<MaterialRestockEntity> getAllMaterialRestock(Date startDate, Date endDate) {
        return materialRestockRepository.findByRestockDateBetweenOrderByRestockDateAsc(startDate, endDate);
    }

    public List<MaterialRestockEntity> getMaterialRestockMaterialRestockId(Date startDate, Date endDate, Integer materialId) {
        return materialRestockRepository.findByRestockDateBetweenAndMaterialId(startDate, endDate, materialId);
    }


    @Transactional
    public MaterialRestockEntity postMaterialRestock(MaterialRestock materialRestock) {
        MaterialRestockEntity materialRestockEntity = new MaterialRestockEntity();
        BeanUtils.copyProperties(materialRestock, materialRestockEntity);

        MaterialEntity material = dataService.getMaterialById(materialRestock.getMaterialId());
        materialRestockEntity.setMaterial(material);

        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialRestock.getMaterialId());
        materialStockEntity.setQuantity(materialStockEntity.getQuantity().add(materialRestock.getQuantity()));
        materialStockEntity.setLastRestockDate(materialRestock.getRestockDate());
        materialStockEntity.setLastRestockQuantity(materialRestock.getQuantity());
        materialStockEntity.setMaterial(material);

        materialStockRepository.save(materialStockEntity);

        return materialRestockRepository.save(materialRestockEntity);

    }


    @Transactional
    public MaterialRestockEntity putMaterialRestockMaterialRestockId(UUID materialRestockId, MaterialRestock materialRestock) {
        if(!materialRestockId.equals(materialRestock.getId()))
            throw new MismatchException("Material Restock Resource ID Mismatch in Put Request");

        MaterialRestockEntity materialRestockEntityInDb = getMaterialRestockById(materialRestockId);

        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialRestock.getMaterialId());
        if(!materialRestockEntityInDb.getQuantity().equals(materialRestock.getQuantity())) {

            materialStockEntity.setQuantity(materialStockEntity.getQuantity()
                    .subtract(materialRestockEntityInDb.getQuantity())
                    .add(materialRestock.getQuantity()));
        }

        BeanUtils.copyProperties(materialRestock, materialRestockEntityInDb, "id", "createdBy", "createdAt");

        if(!materialRestockEntityInDb.getMaterial().getId().equals(materialRestock.getMaterialId())) {
            MaterialEntity materialEntity = dataService.getMaterialById(materialRestock.getMaterialId());
            materialStockEntity.setMaterial(materialEntity);
        }

        materialStockRepository.save(materialStockEntity);
        return materialRestockRepository.save(materialRestockEntityInDb);
    }

    public void deleteMaterialRestockMaterialRestockId(UUID materialRestockId) {

        MaterialRestockEntity materialRestockEntityInDb = getMaterialRestockById(materialRestockId);

        MaterialStockEntity materialStockEntity = dataService.getMaterialStockById(materialRestockEntityInDb.getMaterial().getId());
        materialStockEntity.setQuantity(materialStockEntity.getQuantity().subtract(materialRestockEntityInDb.getQuantity()));
        materialStockRepository.save(materialStockEntity);

        materialRestockRepository.deleteById(materialRestockId);
    }

    private MaterialRestockEntity getMaterialRestockById(UUID materialRestockId) {
        Optional<MaterialRestockEntity> materialRestockEntityInDbOptional = materialRestockRepository.findById(materialRestockId);

        if(materialRestockEntityInDbOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Restock Resource Not Found %s", materialRestockId.toString()));

        return materialRestockEntityInDbOptional.get();
    }
}
