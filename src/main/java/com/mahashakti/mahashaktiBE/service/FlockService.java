package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.FlockEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.FlockRepository;
import com.mahashakti.mahashaktiBE.utils.MaterialStockCalculator;
import com.mahashakti.mahashaktiBe.model.Flock;
import com.mahashakti.mahashaktiBe.model.FlockCount;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlockService {

    private final FlockRepository flockRepository;
    private final MaterialStockCalculator materialStockCalculator;

    public Integer flockCount = 0;

    public FlockEntity addFlock(Flock flock) {

        FlockEntity flockEntity = new FlockEntity();
        BeanUtils.copyProperties(flock, flockEntity);

        flockEntity.setCount(flock.getAdded() ? flock.getCount() : -flock.getCount());

        FlockEntity flockEntitySaved = flockRepository.save(flockEntity);

        flockCount+=flockEntity.getCount();
        materialStockCalculator.updateMinimumStockQuantity(flockCount);

        return flockEntitySaved;
    }

    public FlockEntity getFlock(UUID flockDataId) {
        Optional<FlockEntity> flockEntityOptional = flockRepository.findById(flockDataId);
        if(flockEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Flock Resource Not Found: %s", flockDataId));

        return flockEntityOptional.get();
    }

    public List<FlockEntity> getFlocks() {
        return flockRepository.findAll();
    }

    public void deleteFlock(UUID flockDataID) {
        FlockEntity flockEntity = getFlock(flockDataID);
        flockRepository.deleteById(flockDataID);
        flockCount-=flockEntity.getCount();
        materialStockCalculator.updateMinimumStockQuantity(flockCount);
    }

    public FlockEntity updateFlock(UUID flockDataId, Flock flock) {
        if(!flockDataId.equals(flock.getId()))
            throw new MismatchException("Flock ID Mismatch in Put Request");

        FlockEntity flockEntityInDb = getFlock(flockDataId);

        int prevCount = flockEntityInDb.getCount();

        BeanUtils.copyProperties(flock, flockEntityInDb, "createdBy", "createdAt");

        flockEntityInDb.setCount(flock.getAdded() ? flock.getCount() : -flock.getCount());

        FlockEntity flockEntitySaved = flockRepository.save(flockEntityInDb);

        if(prevCount > 0) flockCount = flockCount - prevCount;
        else flockCount = flockCount + Math.abs(prevCount);

        flockCount += flockEntityInDb.getCount();
        materialStockCalculator.updateMinimumStockQuantity(flockCount);

        return flockEntitySaved;
    }

    @PostConstruct
    public FlockCount getFlockCount() {
        if(flockCount <= 0)
            flockCount = flockRepository.findAll().stream().mapToInt(FlockEntity::getCount).sum();

        return new FlockCount(flockCount);
    }

}
