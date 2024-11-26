package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.entities.ShedEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.OperationalExpenseItemRepository;
import com.mahashakti.mahashaktiBE.repository.UnitRepository;
import com.mahashakti.mahashaktiBE.repository.VendorRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
import com.mahashakti.mahashaktiBE.repository.ShedsRepository;
import com.mahashakti.mahashaktiBe.model.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
    private final MaterialStockRepository materialStockRepository;
    private final ShedsRepository shedsRepository;


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
        return vendorEntityOptional.get();
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

    public List<MaterialStockEntity> getMaterialStock() {
        List<MaterialStockEntity> materialStockEntityList = materialStockRepository.findAll();
        if(materialStockEntityList.isEmpty())
            throw new ResourceNotFoundException("Material Stock Resource Not Found");
        return materialStockEntityList;
    }


    public MaterialStockEntity getMaterialStockById(Integer materialId) {
        Optional<MaterialStockEntity> materialStockEntityOptional = materialStockRepository.findById(materialId);
        if(materialStockEntityOptional.isEmpty())
            throw new ResourceNotFoundException("Material Stock Resource Not Found");
        return materialStockEntityOptional.get();
    }

    public List<ShedEntity> getSheds() {
        return shedsRepository.findAll();
    }

    public ShedEntity getShedById(Integer shedId) {
        Optional<ShedEntity> optionalShedsEntity = shedsRepository.findById(shedId);
        if(optionalShedsEntity.isEmpty())
            throw new ResourceNotFoundException("Shed Resource Not Found");
        return optionalShedsEntity.get();
    }

}
