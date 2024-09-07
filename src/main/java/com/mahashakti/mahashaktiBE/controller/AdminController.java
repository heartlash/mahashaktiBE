package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import com.mahashakti.mahashaktiBE.service.AdminService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.AdminApi;
import com.mahashakti.mahashaktiBe.model.Material;
import com.mahashakti.mahashaktiBe.model.Vendor;
import com.mahashakti.mahashaktiBe.model.Unit;
import com.mahashakti.mahashaktiBe.model.OperationalExpenseItem;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController implements AdminApi {

    private final AdminService adminService;


    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MahashaktiResponse> postAdminAddUnit(Unit unit) {
        UnitEntity unitEntity = adminService.addUnit(unit);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "UNIT ADDED", "SUCCESS", unitEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putAdminUnitUnitId(Integer unitId, Unit unit) {
        UnitEntity unitEntity = adminService.updateUnit(unitId, unit);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "UNIT UPDATED", "SUCCESS", unitEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteAdminUnitUnitId(Integer unitId) {
        adminService.deleteUnit(unitId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "UNIT DELETED", "SUCCESS", null);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<MahashaktiResponse> postAdminAddVendor(Vendor vendor) {
        VendorEntity vendorEntity = adminService.addVendor(vendor);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Vendor ADDED", "SUCCESS", vendorEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putAdminVendorVendorId(Integer vendorId, Vendor vendor) {
        VendorEntity vendorEntity = adminService.updateVendor(vendorId, vendor);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Vendor UPDATED", "SUCCESS", vendorEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteAdminVendorVendorId(Integer vendorId) {
        adminService.deleteVendor(vendorId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Vendor DELETED", "SUCCESS", null);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postAdminAddMaterial(Material material) {
        MaterialEntity materialEntity = adminService.addMaterial(material);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Material ADDED", "SUCCESS", materialEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putAdminMaterialMaterialId(Integer materialId, Material material) {
        MaterialEntity materialEntity = adminService.updateMaterial(materialId, material);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Material UPDATED", "SUCCESS", materialEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteAdminRemoveMaterial(Integer materialId) {
        adminService.deleteMaterial(materialId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material DELETED", "SUCCESS", null);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postAdminAddOperationalExpenseItem(OperationalExpenseItem operationalExpenseItem) {
        OperationalExpenseItemEntity operationalExpenseItemEntity = adminService.addOperationalExpenseItem(operationalExpenseItem);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Operational Expense Item ADDED", "SUCCESS", operationalExpenseItemEntity);
        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);    }

    @Override
    public ResponseEntity<MahashaktiResponse> putAdminOperationalExpenseItemItemId(Integer itemId, OperationalExpenseItem operationalExpenseItem) {
        OperationalExpenseItemEntity operationalExpenseItemEntity = adminService.updateOperationalExpenseItem(itemId, operationalExpenseItem);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Operational Expense Item UPDATED", "SUCCESS", operationalExpenseItemEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteAdminOperationalExpenseItemItemId(Integer itemId) {
        adminService.deleteOperationalExpenseItem(itemId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Operational Expense Item DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

}