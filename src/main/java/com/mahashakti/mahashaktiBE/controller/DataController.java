package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import com.mahashakti.mahashaktiBE.service.DataService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.DataApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class DataController implements DataApi {


    private final DataService dataService;


    @Override
    public ResponseEntity<MahashaktiResponse> getDataMaterial() {

        List<MaterialEntity> materialEntityList = dataService.getMaterials();

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", materialEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getDataMaterialMaterialId(Integer materialId) {
        MaterialEntity materialEntity = dataService.getMaterialById(materialId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", materialEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getDataOperationalExpenseItems() {
        List<OperationalExpenseItemEntity> operationalExpenseItemEntityList = dataService.getOperationalExpenseItems();

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", operationalExpenseItemEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getDataOperationalExpenseItemsItemId(Integer operationalExpenseItemId) {
        OperationalExpenseItemEntity operationalExpenseItemEntity = dataService.getOperationalExpenseItemsById(operationalExpenseItemId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", operationalExpenseItemEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> getDataUnits() {
        List<UnitEntity> unitEntityList = dataService.getUnits();

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", unitEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getDataUnitsUnitId(Integer unitId) {
        UnitEntity unitEntity = dataService.getUnitById(unitId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", unitEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> getDataVendors() {
        List<VendorEntity> vendorEntityList = dataService.getVendors();

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material FetchedD", "SUCCESS", vendorEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getDataVendorsVendorId(Integer vendorId) {
        VendorEntity vendorEntity = dataService.getVendorById(vendorId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Fetched", "SUCCESS", vendorEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postAddVendor(Vendor vendor) {
        VendorEntity vendorEntity = dataService.addVendor(vendor);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Vendor ADDED", "SUCCESS", vendorEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putVendorVendorId(Integer vendorId, Vendor vendor) {
        VendorEntity vendorEntity = dataService.updateVendor(vendorId, vendor);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Vendor UPDATED", "SUCCESS", vendorEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteVendorVendorId(Integer vendorId) {
        dataService.deleteVendor(vendorId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Vendor DELETED", "SUCCESS", null);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

}
