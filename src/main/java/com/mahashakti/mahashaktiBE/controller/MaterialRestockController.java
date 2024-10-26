package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.MaterialRestockEntity;
import com.mahashakti.mahashaktiBE.service.MaterialRestockService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.MaterialRestockApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.MaterialRestock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class MaterialRestockController implements MaterialRestockApi {

    private final MaterialRestockService materialRestockService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAllMaterialRestock(Date startDate, Date endDate) {
        List<MaterialRestockEntity> materialRestockEntityList = materialRestockService.getAllMaterialRestock(startDate, endDate);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Restock FETCHED", "SUCCESS", materialRestockEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
     }

    @Override
    public ResponseEntity<MahashaktiResponse> postMaterialRestock(MaterialRestock materialRestock) {
        MaterialRestockEntity materialRestockEntity = materialRestockService.postMaterialRestock(materialRestock);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Material Restock CREATED", "SUCCESS", materialRestockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialRestockMaterialRestockId(Date startDate, Date endDate, Integer materialRestockId) {
        List<MaterialRestockEntity> materialRestockEntityList = materialRestockService
                .getMaterialRestockMaterialRestockId(startDate, endDate, materialRestockId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Restock FETCHED", "SUCCESS", materialRestockEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putMaterialRestockMaterialRestockId(UUID materialRestockId, MaterialRestock materialRestock) {
        MaterialRestockEntity materialRestockEntity = materialRestockService
                .putMaterialRestockMaterialRestockId(materialRestockId, materialRestock);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Material Restock UPDATED", "SUCCESS", materialRestockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteMaterialRestockMaterialRestockId(UUID materialRestockId) {
        materialRestockService.deleteMaterialRestockMaterialRestockId(materialRestockId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Restock DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }
}
