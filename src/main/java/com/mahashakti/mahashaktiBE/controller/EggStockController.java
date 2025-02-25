package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.EggStockEntity;
import com.mahashakti.mahashaktiBE.service.EggStockService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.EggStockApi;
import com.mahashakti.mahashaktiBe.model.EggStock;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EggStockController implements EggStockApi {

    private final EggStockService eggStockService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAllEggStock() {
        List<EggStockEntity> eggStockEntityList = eggStockService.getAllEggStock();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Egg Stock FETCHED", "SUCCESS", eggStockEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getEggStockById(UUID eggStockId) {
        EggStockEntity EggStockEntity = eggStockService.getEggStockById(eggStockId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Egg Stock FETCHED", "SUCCESS", EggStockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<MahashaktiResponse> getEggStockByEggType(Integer eggTypeId) {
        List<EggStockEntity> eggStockEntityList = eggStockService.getEggStockByEggTypeId(eggTypeId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Egg Stock FETCHED", "SUCCESS", eggStockEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postEggStock(EggStock eggStock) {
        EggStockEntity eggStockEntity = eggStockService.addEggStock(eggStock);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Egg Stock ADDED", "SUCCESS", eggStockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putEggStockById(UUID eggStockId, EggStock eggStock) {
        EggStockEntity eggStockEntity = eggStockService.updateEggStock(eggStockId, eggStock);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Egg Stock UPDATED", "SUCCESS", eggStockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteEggStockById(UUID eggStockId) {
        eggStockService.deleteEggStock(eggStockId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Egg Stock DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }
}
