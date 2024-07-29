package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.service.OperationalExpenseService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.OperationalExpenseApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
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
public class OperationalExpenseController implements OperationalExpenseApi {

    private final OperationalExpenseService operationalExpenseService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAllOperationalExpense(Date startDate, Date endDate, String createdBy) {
        List<OperationalExpenseEntity> operationalExpenseEntityList
                = operationalExpenseService.getAllOperationalExpenses(startDate, endDate, createdBy);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Operational Expense FETCHED", "SUCCESS", operationalExpenseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

    @Override
    public ResponseEntity<MahashaktiResponse> postOperationalExpense(List<OperationalExpense> operationalExpenses) {
        List<OperationalExpenseEntity> operationalExpenseEntityList
                = operationalExpenseService.postOperationalExpenses(operationalExpenses);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense ADDED", "SUCCESS", operationalExpenseEntityList);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getOperationalExpenseOperationalExpenseId(UUID operationalExpenseId) {
        OperationalExpenseEntity operationalExpenseEntity
                = operationalExpenseService.getOperationalExpenseById(operationalExpenseId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Operational Expense FETCHED", "SUCCESS", operationalExpenseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putOperationalExpenseOperationalExpenseId(UUID operationalExpenseId, OperationalExpense operationalExpense) {
        OperationalExpenseEntity operationalExpenseEntity
                = operationalExpenseService.putOperationalExpenseById(operationalExpenseId, operationalExpense);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense UPDATED", "SUCCESS", operationalExpenseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteOperationalExpenseOperationalExpenseId(UUID operationalExpenseId) {
        operationalExpenseService.deleteOperationalExpenseById(operationalExpenseId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


}
