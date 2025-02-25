package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.exception.MahashaktiException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.OperationalExpenseRepository;
import com.mahashakti.mahashaktiBe.model.LatestOperationalExpense;
import com.mahashakti.mahashaktiBe.model.OperationalExpense;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationalExpenseService {

    private final OperationalExpenseRepository operationalExpenseRepository;
    private final DataService dataService;

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

    public Map<Integer, LatestOperationalExpense> getLatestOperationalExpense() {

        List<OperationalExpenseItemEntity> operationalExpenseItemEntityList = dataService.getOperationalExpenseItems();
        
        Map<Integer, LatestOperationalExpense> operationalExpenseItemToLatestExpense = new HashMap<>();
        
        operationalExpenseItemEntityList.forEach(operationalExpenseItemEntity -> {
            
            Optional<OperationalExpenseEntity> operationalExpenseEntityOptional = operationalExpenseRepository
                    .findTopByItemIdOrderByExpenseDateDesc(operationalExpenseItemEntity.getId());
            
            if(operationalExpenseEntityOptional.isPresent()) {
                LatestOperationalExpense latestOperationalExpense = new LatestOperationalExpense();
                BeanUtils.copyProperties(operationalExpenseEntityOptional.get(), latestOperationalExpense);
                operationalExpenseItemToLatestExpense.put(operationalExpenseItemEntity.getId(), latestOperationalExpense);
            }

        });

        return operationalExpenseItemToLatestExpense;

    }

    public List<OperationalExpenseEntity> getOperationalExpenseByItemId(Integer operationalExpenseItemId, Date startDate, Date endDate) {
       
        List<OperationalExpenseEntity> operationalExpenseEntityList = operationalExpenseRepository.findByExpenseDateBetweenAndItemId(startDate, endDate, operationalExpenseItemId);
        
        if(operationalExpenseEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Operational Expense Resource Not Found with Item Id: %d", operationalExpenseItemId));

        return operationalExpenseEntityList;
    }

}
