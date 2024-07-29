package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.service.ExpenseService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.ExpenseApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.MaterialPurchase;
import com.mahashakti.mahashaktiBe.model.OperationalExpense;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExpenseController implements ExpenseApi {

    private final ExpenseService expenseService;


    @Override
    public ResponseEntity<MahashaktiResponse> getAllMaterialExpense(Date startDate, Date endDate, String createdBy) {
        List<MaterialPurchaseEntity> materialPurchaseEntityList = expenseService
                .getAllMaterialPurchaseExpenses(startDate, endDate, createdBy);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialPurchaseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postMaterialExpense(List<MaterialPurchase> materialPurchase) {

        List<MaterialPurchaseEntity> materialPurchaseEntityList = expenseService.postMaterialPurchaseExpenses(materialPurchase);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Material Expense ADDED", "SUCCESS", materialPurchaseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialExpenseMaterialPurchaseId(UUID materialPurchaseId) {

        MaterialPurchaseEntity materialPurchaseEntity = expenseService.getMaterialPurchaseExpenseById(materialPurchaseId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialPurchaseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialExpenseMaterialId(Integer materialId) {
        List<MaterialPurchaseEntity> materialPurchaseEntityList = expenseService.getMaterialPurchaseExpenseByMaterialId(materialId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialPurchaseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> putMaterialExpenseMaterialPurchaseId(UUID materialPurchaseId, MaterialPurchase materialPurchase) {
        MaterialPurchaseEntity materialPurchaseEntity = expenseService.putMaterialPurchaseExpenseById(materialPurchaseId, materialPurchase);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Material Expense UPDATED", "SUCCESS", materialPurchaseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteMaterialExpenseMaterialPurchaseId(UUID materialPurchaseId) {
        expenseService.deleteMaterialPurchaseExpenseById(materialPurchaseId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getAllOperationExpense(Date startDate, Date endDate, String createdBy) {
        List<OperationalExpenseEntity> operationalExpenseEntityList
                = expenseService.getAllOperationalExpenses(startDate, endDate, createdBy);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Operational Expense FETCHED", "SUCCESS", operationalExpenseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postOperationExpense(List<OperationalExpense> operationExpenses) {
        List<OperationalExpenseEntity> operationalExpenseEntityList
                = expenseService.postOperationalExpenses(operationExpenses);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense ADDED", "SUCCESS", operationalExpenseEntityList);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getOperationExpenseOperationalExpenseId(UUID operationExpenseId) {
        OperationalExpenseEntity operationalExpenseEntity
                = expenseService.getOperationalExpenseById(operationExpenseId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Operational Expense FETCHED", "SUCCESS", operationalExpenseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> putOperationExpenseOperationalExpenseId(UUID operationExpenseId, OperationalExpense operationExpense) {
        OperationalExpenseEntity operationalExpenseEntity
                = expenseService.putOperationalExpenseById(operationExpenseId, operationExpense);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense UPDATED", "SUCCESS", operationalExpenseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteOperationExpenseOperationalExpenseId(UUID operationExpenseId) {
        expenseService.deleteOperationalExpenseById(operationExpenseId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

}
