package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.service.SaleService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.SaleApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.Sale;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SaleController implements SaleApi {

    private final SaleService saleService;

    @Override
    public ResponseEntity<MahashaktiResponse> getSale(Date startDate, Date endDate, Integer vendorId, Boolean paid) {
        List<SaleEntity> saleEntityList = saleService.getAllSale(startDate, endDate, vendorId, paid);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Sale FETCHED", "SUCCESS", saleEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postSale(List<Sale> sale) {
        List<SaleEntity> saleEntityList = saleService.postSale(sale);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Sale FETCHED", "SUCCESS", saleEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);      }

    @Override
    public ResponseEntity<MahashaktiResponse> getSaleSaleId(UUID saleId) {
        SaleEntity saleEntity = saleService.getSaleById(saleId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Sale FETCHED", "SUCCESS", saleEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);      }

    @Override
    public ResponseEntity<MahashaktiResponse> putSaleSaleId(UUID saleId, Sale sale) {
        SaleEntity saleEntity = saleService.putSaleById(saleId, sale);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Sale UPDATED", "SUCCESS", saleEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> deleteSaleSaleId(UUID saleId) {
        saleService.deleteSaleById(saleId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Sale DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getSaleLatest() {
        List<SaleEntity> saleEntity = saleService.getSaleLatest();

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Latest Sale Fetched", "SUCCESS", saleEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }
}
