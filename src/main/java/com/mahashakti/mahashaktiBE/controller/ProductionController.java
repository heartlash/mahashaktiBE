package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.ProductionEntity;
import com.mahashakti.mahashaktiBE.service.ProductionService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.ProductionApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.Production;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductionController implements ProductionApi {

    private final ProductionService productionService;

    @Override
    public ResponseEntity<MahashaktiResponse> getProduction(Date startDate, Date endDate) {
        List<ProductionEntity> productionEntityList
                = productionService.getAllProduction(startDate, endDate);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Production FETCHED", "SUCCESS", productionEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postProduction(Production production) {
        ProductionEntity productionEntity
                = productionService.postProduction(production);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Production ADDED", "SUCCESS", productionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getProductionProductionId(UUID productionId) {
        ProductionEntity productionEntity
                = productionService.getProductionById(productionId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Production FETCHED", "SUCCESS", productionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putProductionProductionId(UUID productionId, Production production) {
        ProductionEntity productionEntity
                = productionService.putProductionById(productionId, production);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Production UPDATED", "SUCCESS", productionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteProductionProductionId(UUID productionId) {

        productionService.deleteProductionById(productionId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Production DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }
}
