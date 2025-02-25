package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.constants.EggType;
import com.mahashakti.mahashaktiBE.entities.EggStockEntity;
import com.mahashakti.mahashaktiBE.entities.EggTypeEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.EggStockRepository;
import com.mahashakti.mahashaktiBe.model.EggStock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EggStockService {

    private final EggStockRepository eggStockRepository;
    private final DataService dataService;
    private final AnalyticsService analyticsService;


    public EggStockEntity addEggStock(EggStock eggStock) {

        EggStockEntity eggStockEntity = new EggStockEntity();
        BeanUtils.copyProperties(eggStock, eggStockEntity);

        eggStockEntity.setCount(eggStock.getAdded() ? eggStock.getCount() : -eggStock.getCount());
        eggStockEntity.setEggType(dataService.getEggTypeById(eggStock.getEggTypeId()));

        eggStockEntity = eggStockRepository.save(eggStockEntity);

        analyticsService.incrementEggStockCount(eggStockEntity.getCount(), EggType.valueOf(eggStockEntity.getEggType().getName()));

        return eggStockEntity;
    }

    public List<EggStockEntity> getAllEggStock() {
        return eggStockRepository.findAll();

    }

    public EggStockEntity getEggStockById(UUID eggStockDataId) {
        Optional<EggStockEntity> eggStockEntityOptional = eggStockRepository.findById(eggStockDataId);
        if(eggStockEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Egg Stock Resource Not Found: %s", eggStockDataId));

        return eggStockEntityOptional.get();
    }

    public List<EggStockEntity> getEggStockByEggTypeId(Integer eggTypeId) {
        return eggStockRepository.findByEggTypeId(eggTypeId);
    }

    public void deleteEggStock(UUID eggStockDataId) {
        EggStockEntity eggStockEntity = getEggStockById(eggStockDataId);

        analyticsService.decrementEggStockCount(eggStockEntity.getCount(), EggType.valueOf(eggStockEntity.getEggType().getName()));
        eggStockRepository.deleteById(eggStockDataId);
    }

    public EggStockEntity updateEggStock(UUID eggStockDataId, EggStock eggStock) {
        if(!eggStockDataId.equals(eggStock.getId()))
            throw new MismatchException("Egg Stock ID Mismatch in Put Request");

        EggStockEntity eggStockEntityInDb = getEggStockById(eggStockDataId);

        Integer originalCount = eggStockEntityInDb.getCount();
        EggTypeEntity eggTypeEntity = eggStockEntityInDb.getEggType();

        BeanUtils.copyProperties(eggStock, eggStockEntityInDb, "createdBy", "createdAt");

        eggStockEntityInDb.setCount(eggStock.getAdded() ? eggStock.getCount() : -eggStock.getCount());
        eggStockEntityInDb.setEggType(dataService.getEggTypeById(eggStock.getEggTypeId()));

        EggStockEntity eggStockEntitySaved = eggStockRepository.save(eggStockEntityInDb);

        analyticsService.decrementEggStockCount(originalCount,
                EggType.valueOf(eggTypeEntity.getName()));

        analyticsService.incrementEggStockCount(eggStockEntitySaved.getCount(),
                EggType.valueOf(eggStockEntitySaved.getEggType().getName()));

        return eggStockEntitySaved;

    }

}
