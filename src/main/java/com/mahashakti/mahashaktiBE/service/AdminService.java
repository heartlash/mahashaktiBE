package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import com.mahashakti.mahashaktiBE.entities.ShedEntity;
import com.mahashakti.mahashaktiBE.entities.EggTypeEntity;
import com.mahashakti.mahashaktiBE.exception.InvalidDataStateException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.OperationalExpenseItemRepository;
import com.mahashakti.mahashaktiBE.repository.ShedsRepository;
import com.mahashakti.mahashaktiBE.repository.UnitRepository;
import com.mahashakti.mahashaktiBE.repository.EggTypeRepository;
import com.mahashakti.mahashaktiBe.model.Material;
import com.mahashakti.mahashaktiBe.model.OperationalExpenseItem;
import com.mahashakti.mahashaktiBe.model.Unit;
import com.mahashakti.mahashaktiBe.model.Shed;
import com.mahashakti.mahashaktiBe.model.EggType;
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
    private final MaterialRepository materialRepository;
    private final OperationalExpenseItemRepository operationalExpenseItemRepository;
    private final ShedsRepository shedsRepository;
    private final FlockService flockService;
    private final EggTypeRepository eggTypeRepository;
    private final AnalyticsService analyticsService;
    private final DataService dataService;


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

    public ShedEntity addShed(Shed shed) {
        ShedEntity shedEntity = new ShedEntity();
        BeanUtils.copyProperties(shed, shedEntity);
        return shedsRepository.save(shedEntity);
    }

    public ShedEntity updateShed(Integer shedId, Shed shed) {

        if(!shedId.equals(shed.getId())) throw new MismatchException("Shed ID Mismatch in Put Request");

        Optional<ShedEntity> shedOptional = shedsRepository.findById(shedId);
        if(shedOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Shed Resource Not Found: %d", shedId));

        ShedEntity shedEntity = new ShedEntity();
        BeanUtils.copyProperties(shed, shedEntity);
        return shedsRepository.save(shedEntity);
    }

    public void deleteShed(Integer shedId) {
        if(flockService.getFlockShedCount(shedId).getCount() == 0) {
            Optional<ShedEntity> shedOptional = shedsRepository.findById(shedId);
            if (shedOptional.isEmpty())
                throw new ResourceNotFoundException(String.format("Shed Resource Not Found: %d", shedId));
            shedsRepository.deleteById(shedId);
        } else {
            throw new InvalidDataStateException(String.format("Flock count not zero for :%d", shedId));
        }

    }

    public EggTypeEntity addEggType(EggType eggType) {
        EggTypeEntity eggTypeEntity = new EggTypeEntity();
        BeanUtils.copyProperties(eggType, eggTypeEntity);
        return eggTypeRepository.save(eggTypeEntity);
    }

    public EggTypeEntity updateEggType(Integer eggTypeId, EggType eggType) {

        if(!eggTypeId.equals(eggType.getId())) throw new MismatchException("Egg Type ID Mismatch in Put Request");

        Optional<EggTypeEntity> eggTypeEntityOptional = eggTypeRepository.findById(eggTypeId);
        if(eggTypeEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Egg Type Resource Not Found: %d", eggTypeId));

        EggTypeEntity eggTypeEntity = new EggTypeEntity();
        BeanUtils.copyProperties(eggType, eggTypeEntity);
        return eggTypeRepository.save(eggTypeEntity);
    }

    public void deleteEggType(Integer eggTypeId) {
        // not sure if ever going to delete egg type
        /*
        EggTypeEntity eggTypeEntity = dataService.getEggTypeById(eggTypeId);
        if(eggTypeEntity.getName().equals("GRADE_A"))
            if(analyticsService.getAnalyticsEggStock().getGRADEA() == 0) {
            Optional<EggTypekEntity> shedOptional = shedsRepository.findById(shedId);
            if (shedOptional.isEmpty())
                throw new ResourceNotFoundException(String.format("Shed Resource Not Found: %d", shedId));
            shedsRepository.deleteById(shedId);
        } else {
            throw new InvalidDataStateException(String.format("Flock count not zero for :%d", shedId));
        }

         */
    }

}
