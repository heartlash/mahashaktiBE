package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.OperationalExpenseItemRepository;
import com.mahashakti.mahashaktiBE.repository.UnitRepository;
import com.mahashakti.mahashaktiBE.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataService {

    private final UnitRepository unitRepository;
    private final VendorRepository vendorRepository;
    private final MaterialRepository materialRepository;
    private final OperationalExpenseItemRepository operationalExpenseItemRepository;


    public List<MaterialEntity> getMaterials() {
        return materialRepository.findAll();
    }

    public MaterialEntity getMaterialById(Integer materialId) {
        Optional<MaterialEntity> materialEntityOptional = materialRepository.findById(materialId);
        if(materialEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Material Resource Not Found: %d", materialId));
        return materialEntityOptional.get();
    }

    public List<OperationalExpenseItemEntity> getOperationalExpenseItems() {
        return operationalExpenseItemRepository.findAll();
    }

    public OperationalExpenseItemEntity getOperationalExpenseItemsById(Integer operationalExpenseItemId) {
        Optional<OperationalExpenseItemEntity> operationalExpenseItemEntityOptional
                = operationalExpenseItemRepository.findById(operationalExpenseItemId);
        if(operationalExpenseItemEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Operational Expense Item Resource Not Found: %d", operationalExpenseItemId));
        return operationalExpenseItemEntityOptional.get();    }

    public List<UnitEntity> getUnits() {
        return unitRepository.findAll();
    }

    public UnitEntity getUnitById(Integer unitId) {
        Optional<UnitEntity> unitEntityOptional = unitRepository.findById(unitId);
        if(unitEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Unit Resource Not Found: %d", unitId));
        return unitEntityOptional.get();    }

    public List<VendorEntity> getVendors() {
        return vendorRepository.findAll();
    }

    public VendorEntity getVendorById(Integer vendorId) {
        Optional<VendorEntity> vendorEntityOptional = vendorRepository.findById(vendorId);
        if(vendorEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Vendor Resource Not Found: %d", vendorId));
        return vendorEntityOptional.get();    }

}
