package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.exception.MahashaktiException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialPurchaseRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.OperationalExpenseRepository;
import com.mahashakti.mahashaktiBe.model.MaterialPurchase;
import com.mahashakti.mahashaktiBe.model.OperationalExpense;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final MaterialPurchaseRepository materialPurchaseRepository;
    private final OperationalExpenseRepository operationalExpenseRepository;
    private final DataService dataService;
    private final MaterialRepository materialRepository;


    public List<MaterialPurchaseEntity> getAllMaterialPurchaseExpenses(Date startDate, Date endDate, String createdBy) {
        if(Objects.isNull(createdBy)) return materialPurchaseRepository.findByPurchaseDateBetween(startDate, endDate);
        else return materialPurchaseRepository.findByPurchaseDateBetweenAndCreatedBy(startDate, endDate, createdBy);
    }

    public MaterialPurchaseEntity getMaterialPurchaseExpenseById(UUID materialPurchaseId) {
        Optional<MaterialPurchaseEntity> materialPurchaseEntityOptional = materialPurchaseRepository.findById(materialPurchaseId);
        if(materialPurchaseEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Expense Resource Not Found: %s", materialPurchaseId.toString()));

        return materialPurchaseEntityOptional.get();
    }

    public List<MaterialPurchaseEntity> getMaterialPurchaseExpenseByMaterialId(Integer materialId) {
        List<MaterialPurchaseEntity> materialPurchaseEntityList = materialPurchaseRepository.findByMaterialId(materialId);
        if(materialPurchaseEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Expense Resource Not Found with Material Id: %d", materialId));

        return materialPurchaseEntityList;
    }


    @Transactional
    public List<MaterialPurchaseEntity> postMaterialPurchaseExpenses(List<MaterialPurchase> materialPurchases) {

        List<MaterialEntity> materialEntityList = new ArrayList<>();

        List<MaterialPurchaseEntity> materialPurchaseEntityList =  materialPurchases.stream()
                .map(materialPurchase -> {
                    MaterialPurchaseEntity materialPurchaseEntity = new MaterialPurchaseEntity();
                    try {
                        BeanUtils.copyProperties(materialPurchase, materialPurchaseEntity);
                        MaterialEntity materialEntity = dataService.getMaterialById(materialPurchase.getMaterialId());

                        materialEntity.setSku(materialEntity.getSku().add(materialPurchase.getQuantity()));
                        materialPurchaseEntity.setMaterial(materialEntity);
                        materialEntityList.add(materialEntity);
                    } catch (Exception e) {
                        throw new MahashaktiException();
                    }
                    return materialPurchaseEntity;
                })
                .collect(Collectors.toList());

        materialRepository.saveAll(materialEntityList);

        return materialPurchaseRepository.saveAll(materialPurchaseEntityList);

    }

    public MaterialPurchaseEntity putMaterialPurchaseExpenseById(UUID materialPurchaseId, MaterialPurchase materialPurchase) {
        if(!materialPurchaseId.equals(materialPurchase.getId()))
            throw new MismatchException("Material Purchase Expense ID Mismatch in Put Request");

        MaterialPurchaseEntity materialPurchaseEntityInDb = getMaterialPurchaseExpenseById(materialPurchaseId);


        if(!materialPurchaseEntityInDb.getQuantity().equals(materialPurchase.getQuantity())) {
            MaterialEntity materialEntity = dataService.getMaterialById(materialPurchase.getMaterialId());

            materialEntity.setSku(materialEntity.getSku()
                    .subtract(materialPurchaseEntityInDb.getMaterial().getSku())
                    .add(materialPurchase.getQuantity()));
            materialPurchaseEntityInDb.setMaterial(materialEntity);
            materialRepository.save(materialEntity);
        }

        BeanUtils.copyProperties(materialPurchase, materialPurchaseEntityInDb, "createdBy", "createdAt");

        if(!materialPurchaseEntityInDb.getMaterial().getId().equals(materialPurchase.getMaterialId()))
            materialPurchaseEntityInDb.setMaterial(dataService.getMaterialById(materialPurchase.getMaterialId()));


        return materialPurchaseRepository.save(materialPurchaseEntityInDb);

    }

    @Transactional
    public void deleteMaterialPurchaseExpenseById(UUID materialPurchaseId) {
        MaterialPurchaseEntity materialPurchaseEntity = getMaterialPurchaseExpenseById(materialPurchaseId);
        MaterialEntity materialEntity = dataService.getMaterialById(materialPurchaseEntity.getMaterial().getId());

        materialEntity.setSku(materialEntity.getSku().subtract(materialPurchaseEntity.getQuantity()));
        materialRepository.save(materialEntity);
        materialPurchaseRepository.deleteById(materialPurchaseId);
    }

    public List<OperationalExpenseEntity> getAllOperationalExpenses(Date startDate, Date endDate, String createdBy) {
        if(Objects.isNull(createdBy)) return operationalExpenseRepository.findByExpenseDateBetween(startDate, endDate);
        else return operationalExpenseRepository.findByExpenseDateBetweenAndCreatedBy(startDate, endDate, createdBy);
    }

    public OperationalExpenseEntity getOperationalExpenseById(UUID operationalExpenseId) {
        Optional<OperationalExpenseEntity> operationalExpenseEntityOptional = operationalExpenseRepository.findById(operationalExpenseId);
        if(operationalExpenseEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Operational Expense Resource Not Found: %s", operationalExpenseId.toString()));

        return operationalExpenseEntityOptional.get();
    }

    @Transactional
    public List<OperationalExpenseEntity> postOperationalExpenses(List<OperationalExpense> operationalExpenses) {
        List<OperationalExpenseEntity> operationalExpenseEntityList =  operationalExpenses.stream()
                .map(operationalExpense -> {
                    OperationalExpenseEntity operationalExpenseEntity = new OperationalExpenseEntity();
                    try {
                        BeanUtils.copyProperties(operationalExpense, operationalExpenseEntity);
                        operationalExpenseEntity.setItem(dataService.getOperationalExpenseItemsById(operationalExpense.getItemId()));
                    } catch (Exception e) {
                        throw new MahashaktiException();
                    }
                    return operationalExpenseEntity;
                })
                .collect(Collectors.toList());
        return operationalExpenseRepository.saveAll(operationalExpenseEntityList);

    }

    public OperationalExpenseEntity putOperationalExpenseById(UUID operationalExpenseId, OperationalExpense operationalExpense) {
        if(!operationalExpenseId.equals(operationalExpense.getId()))
            throw new MismatchException("Operational Expense ID Mismatch in Put Request");

        OperationalExpenseEntity operationalExpenseEntityInDb = getOperationalExpenseById(operationalExpenseId);

        BeanUtils.copyProperties(operationalExpense, operationalExpenseEntityInDb, "createdBy", "createdAt");

        if(!operationalExpenseEntityInDb.getItem().getId().equals(operationalExpense.getItemId()))
            operationalExpenseEntityInDb.setItem(dataService.getOperationalExpenseItemsById(operationalExpense.getItemId()));

        return operationalExpenseRepository.save(operationalExpenseEntityInDb);

    }

    public void deleteOperationalExpenseById(UUID operationalExpenseId) {
        getOperationalExpenseById(operationalExpenseId);
        operationalExpenseRepository.deleteById(operationalExpenseId);
    }
}
