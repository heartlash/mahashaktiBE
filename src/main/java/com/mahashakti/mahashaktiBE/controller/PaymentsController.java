package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.PaymentsEntity;
import com.mahashakti.mahashaktiBE.service.PaymentsService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.PaymentsApi;
import com.mahashakti.mahashaktiBe.model.LatestPayments;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.Payment;
import com.mahashakti.mahashaktiBe.model.SaleCredit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentsController implements PaymentsApi {

    private final PaymentsService paymentsService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAllPayments(Date startDate, Date endDate) {
        List<PaymentsEntity> paymentsEntityList = paymentsService
                .getAllPayments(startDate, endDate);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Payments FETCHED", "SUCCESS", paymentsEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postPayment(Payment payment) {
        PaymentsEntity paymentsEntity = paymentsService.postPayment(payment);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Payments ADDED", "SUCCESS", paymentsEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getPaymentById(UUID paymentId) {
        PaymentsEntity paymentsEntity  = paymentsService.getPaymentById(paymentId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Payments FETCHED", "SUCCESS", paymentsEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getPaymentByVendorId(Integer vendorId, Date startDate, Date endDate) {
        List<PaymentsEntity> paymentsEntityList = paymentsService.getPaymentByVendorId(vendorId, startDate, endDate);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Payments FETCHED", "SUCCESS", paymentsEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putPaymentsPaymentId(UUID paymentId, Payment payment) {
        PaymentsEntity paymentsEntity = paymentsService.putPaymentsPaymentId(paymentId, payment);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Payments UPDATED", "SUCCESS", paymentsEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deletePaymentsPaymentId(UUID paymentId) {
        paymentsService.deletePaymentsPaymentId(paymentId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Payments DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getLatestPayments() {
        Map<Integer, LatestPayments> latestPaymentMap = paymentsService.getLatestPayments();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Latest Payments FETCHED", "SUCCESS", latestPaymentMap);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getPaymentsPopulate() {
        List<PaymentsEntity> paymentsEntityList = paymentsService.populatePayments();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Payments Populated", "SUCCESS", paymentsEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


}
