package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.OperationalExpenseItemRepository;
import com.mahashakti.mahashaktiBE.repository.UnitRepository;
import com.mahashakti.mahashaktiBE.repository.VendorRepository;
import com.mahashakti.mahashaktiBe.model.Material;
import com.mahashakti.mahashaktiBe.model.OperationalExpenseItem;
import com.mahashakti.mahashaktiBe.model.Unit;
import com.mahashakti.mahashaktiBe.model.Vendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UnitRepository unitRepository;
    private final VendorRepository vendorRepository;
    private final MaterialRepository materialRepository;
    private final OperationalExpenseItemRepository operationalExpenseItemRepository;


    public UnitEntity addUnit(Unit unit) {
        UnitEntity unitEntity = new UnitEntity();
        BeanUtils.copyProperties(unit, unitEntity);
        return unitRepository.save(unitEntity);

    }

    public UnitEntity updateUnit(Integer unitId, Unit unit) {

        if(!unitId.equals(unit.getId())) throw new MismatchException("Unit ID Mismatch in Put Request");

        Optional<UnitEntity> unitsOptional = unitRepository.findById(unitId);
        if(unitsOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Unit Resource Not Found: %d", unitId));
        UnitEntity unitEntity = new UnitEntity();

        BeanUtils.copyProperties(unit, unitEntity);
        return unitRepository.save(unitEntity);

    }

    public void deleteUnit(Integer unitId) {
        Optional<UnitEntity> unitsOptional = unitRepository.findById(unitId);
        if(unitsOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Unit Resource Not Found: %d", unitId));
        unitRepository.deleteById(unitId);

    }

    public VendorEntity addVendor(Vendor vendor) {
        VendorEntity vendorEntity = new VendorEntity();
        BeanUtils.copyProperties(vendor, vendorEntity);
        return vendorRepository.save(vendorEntity);
    }


    public VendorEntity updateVendor(Integer vendorId, Vendor vendor) {

        if(!vendorId.equals(vendor.getId())) throw new MismatchException("Vendor ID Mismatch in Put Request");

        Optional<VendorEntity> unitsOptional = vendorRepository.findById(vendorId);
        if(unitsOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Vendor Resource Not Found: %d", vendorId));

        VendorEntity vendorEntity = new VendorEntity();
        BeanUtils.copyProperties(vendor, vendorEntity);
        return vendorRepository.save(vendorEntity);
    }

    public void deleteVendor(Integer vendorId) {

        Optional<VendorEntity> vendorOptional = vendorRepository.findById(vendorId);
        if(vendorOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Vendor Resource Not Found: %d", vendorId));
        vendorRepository.deleteById(vendorId);

    }

    public MaterialEntity addMaterial(Material material) {
        MaterialEntity materialEntity = new MaterialEntity();
        BeanUtils.copyProperties(material, materialEntity, "unit");

        Optional<UnitEntity> unitsOptional = unitRepository.findById(material.getUnitId());
        if(unitsOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Unit Resource Not Found: %d", material.getUnitId()));
        materialEntity.setUnit(unitsOptional.get());

        return materialRepository.save(materialEntity);

    }

    public MaterialEntity updateMaterial(Integer materialId, Material material) {

        if(!materialId.equals(material.getId())) throw new MismatchException("Material ID Mismatch in Put Request");

        Optional<MaterialEntity> materialEntityOptional = materialRepository.findById(materialId);
        if(materialEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Resource Not Found: %d", materialId));

        Optional<UnitEntity> unitsOptional = unitRepository.findById(material.getUnitId());
        if(unitsOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Unit Resource Not Found: %d", material.getUnitId()));

        MaterialEntity materialEntity = new MaterialEntity();
        BeanUtils.copyProperties(material, materialEntity);
        materialEntity.setUnit(unitsOptional.get());

        return materialRepository.save(materialEntity);

    }

    public void deleteMaterial(Integer materialId) {
        Optional<MaterialEntity> materialEntityOptional = materialRepository.findById(materialId);
        if(materialEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Unit Resource Not Found: %d", materialId));
        materialRepository.deleteById(materialId);
    }

    public OperationalExpenseItemEntity addOperationalExpenseItem(OperationalExpenseItem operationalExpenseItem) {
        OperationalExpenseItemEntity operationalExpenseItemEntity = new OperationalExpenseItemEntity();
        BeanUtils.copyProperties(operationalExpenseItem, operationalExpenseItemEntity);
        return operationalExpenseItemRepository.save(operationalExpenseItemEntity);

    }

    public OperationalExpenseItemEntity updateOperationalExpenseItem(Integer operationalExpenseItemId, OperationalExpenseItem operationalExpenseItem) {

        if(!operationalExpenseItemId.equals(operationalExpenseItem.getId()))
            throw new MismatchException("Operational Expense Item ID Mismatch in Put Request");

        Optional<OperationalExpenseItemEntity> operationalExpenseItemEntityOptional = operationalExpenseItemRepository
                .findById(operationalExpenseItemId);

        if(operationalExpenseItemEntityOptional.isEmpty())
            throw new ResourceNotFoundException(
                    String.format("Operational Expense Item Resource Not Found: %d", operationalExpenseItemId));

        OperationalExpenseItemEntity operationalExpenseItemEntity = new OperationalExpenseItemEntity();

        BeanUtils.copyProperties(operationalExpenseItem, operationalExpenseItemEntity);
        return operationalExpenseItemRepository.save(operationalExpenseItemEntity);

    }

    public void deleteOperationalExpenseItem(Integer operationalExpenseItemId) {
        Optional<OperationalExpenseItemEntity> operationalExpenseItemEntityOptional = operationalExpenseItemRepository
                .findById(operationalExpenseItemId);

        if(operationalExpenseItemEntityOptional.isEmpty())
            throw new ResourceNotFoundException(
                    String.format("Operational Expense Item Resource Not Found: %d", operationalExpenseItemId));

        operationalExpenseItemRepository.deleteById(operationalExpenseItemId);

    }

}
