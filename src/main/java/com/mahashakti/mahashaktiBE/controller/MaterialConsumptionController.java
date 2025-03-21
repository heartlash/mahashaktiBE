package com.mahashakti.mahashaktiBE.controller;


import com.mahashakti.mahashaktiBE.entities.MaterialConsumptionEntity;
import com.mahashakti.mahashaktiBE.service.MaterialConsumptionService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.MaterialConsumptionApi;
import com.mahashakti.mahashaktiBe.model.DailyConsumption;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.MaterialConsumption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class MaterialConsumptionController implements MaterialConsumptionApi {

    private final MaterialConsumptionService materialConsumptionService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAllMaterialConsumption(Date startDate, Date endDate) {
        List<MaterialConsumptionEntity> materialConsumptionEntityList
                = materialConsumptionService.getAllMaterialConsumption(startDate, endDate);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Consumption FETCHED", "SUCCESS", materialConsumptionEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postMaterialConsumption(List<MaterialConsumption> materialConsumptions) {
        List<MaterialConsumptionEntity> materialConsumptionEntityList
                = materialConsumptionService.postMaterialConsumptions(materialConsumptions);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Material Consumption ADDED", "SUCCESS", materialConsumptionEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialConsumptionConsumptionId(UUID consumptionId) {
        MaterialConsumptionEntity materialConsumptionEntity
                = materialConsumptionService.getMaterialConsumptionById(consumptionId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Consumption FETCHED", "SUCCESS", materialConsumptionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialConsumptionMaterialId(Integer materialId, Date startDate, Date endDate) {
        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptionService.getMaterialConsumptionByMaterialId(materialId, startDate, endDate);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Expense FETCHED", "SUCCESS", materialConsumptionEntityList);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
}

    @Override
    public ResponseEntity<MahashaktiResponse> putMaterialConsumptionConsumptionId(UUID consumptionId, MaterialConsumption materialConsumption) {
        MaterialConsumptionEntity materialConsumptionEntity
                = materialConsumptionService.putMaterialConsumption(consumptionId, materialConsumption);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Material Consumption UPDATED", "SUCCESS", materialConsumptionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteMaterialConsumptionConsumptionId(UUID consumptionId) {
        materialConsumptionService.deleteMaterialConsumptionById(consumptionId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Consumption DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postDailyMaterialConsumption(DailyConsumption dailyConsumption) {

        Map<Integer, Integer> shedToFlock = Collections.singletonMap(
                dailyConsumption.getShedId(),
                dailyConsumption.getFlockCount());

        List<MaterialConsumptionEntity> materialConsumptionEntityList = materialConsumptionService.postDailyMaterialConsumption(
                shedToFlock, dailyConsumption.getProductionDate());

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Daily Material Consumption CREATED", "SUCCESS", materialConsumptionEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteMaterialConsumptionDaily(DailyConsumption dailyConsumption) {
        materialConsumptionService.deleteMaterialConsumptionByConsumptionDate(dailyConsumption.getShedId(), dailyConsumption.getProductionDate());

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Daily Material Consumption DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);

    }
}
