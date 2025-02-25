package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.service.MaterialPurchaseService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.MaterialPurchaseApi;
import com.mahashakti.mahashaktiBe.model.LatestMaterialPurchase;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.MaterialPurchase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class MaterialPurchaseController implements MaterialPurchaseApi {

    private final MaterialPurchaseService materialPurchaseService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAllMaterialPurchase(Date startDate, Date endDate, String createdBy) {
        List<MaterialPurchaseEntity> materialPurchaseEntityList = materialPurchaseService
                .getAllMaterialPurchases(startDate, endDate, createdBy);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialPurchaseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postMaterialPurchase(List<MaterialPurchase> materialPurchase) {

        List<MaterialPurchaseEntity> materialPurchaseEntityList = materialPurchaseService.postMaterialPurchases(materialPurchase);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Material Expense ADDED", "SUCCESS", materialPurchaseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);    
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialPurchaseMaterialPurchaseId(UUID materialPurchaseId) {
        MaterialPurchaseEntity materialPurchaseEntity = materialPurchaseService.getMaterialPurchaseById(materialPurchaseId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialPurchaseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);   
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialPurchaseMaterialId(Integer materialId, Date startDate, Date endDate) {
        List<MaterialPurchaseEntity> materialPurchaseEntityList = materialPurchaseService.getMaterialPurchaseByMaterialId(materialId, startDate, endDate);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialPurchaseEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putMaterialPurchaseMaterialPurchaseId(UUID materialPurchaseId, MaterialPurchase materialPurchase) {
        MaterialPurchaseEntity materialPurchaseEntity = materialPurchaseService.putMaterialPurchaseById(materialPurchaseId, materialPurchase);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Material Expense UPDATED", "SUCCESS", materialPurchaseEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);    
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteMaterialPurchaseMaterialPurchaseId(UUID materialPurchaseId) {
        materialPurchaseService.deleteMaterialPurchaseById(materialPurchaseId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getLatestMaterialPurchase() {
        Map<Integer, LatestMaterialPurchase> latestMaterialPurchaseMap = materialPurchaseService.getLatestMaterialPurchase();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Latest Material Expense FETCHED", "SUCCESS", latestMaterialPurchaseMap);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

}
