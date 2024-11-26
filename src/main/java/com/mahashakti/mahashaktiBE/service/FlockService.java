package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.FlockEntity;
import com.mahashakti.mahashaktiBE.entities.ShedEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.FlockRepository;
import com.mahashakti.mahashaktiBE.utils.MaterialStockCalculator;
import com.mahashakti.mahashaktiBe.model.Flock;
import com.mahashakti.mahashaktiBe.model.FlockCount;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class FlockService {

    private final FlockRepository flockRepository;
    private final MaterialStockCalculator materialStockCalculator;
    private final DataService dataService;

    public Integer flockCount = 0;
    public HashMap<Integer, Integer> flockCountMap = new HashMap<>();

    public FlockEntity addFlock(Flock flock) {

        FlockEntity flockEntity = new FlockEntity();
        BeanUtils.copyProperties(flock, flockEntity);

        flockEntity.setCount(flock.getAdded() ? flock.getCount() : -flock.getCount());
        flockEntity.setShed(dataService.getShedById(flock.getShedId()));
        FlockEntity flockEntitySaved = flockRepository.save(flockEntity);

        flockCount+=flockEntity.getCount();
        flockCountMap.put(flock.getShedId(), flockCountMap.getOrDefault(flock.getShedId(), 0) + flockEntity.getCount());

        materialStockCalculator.updateMinimumStockQuantity(flockCountMap);

        return flockEntitySaved;
    }

    public FlockEntity getFlock(UUID flockDataId) {
        Optional<FlockEntity> flockEntityOptional = flockRepository.findById(flockDataId);
        if(flockEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Flock Resource Not Found: %s", flockDataId));

        return flockEntityOptional.get();
    }

    public List<FlockEntity> getFlocks(Date startDate, Date endDate, Integer shedId) {
        if(shedId == null) return  flockRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        else return flockRepository.findByDateBetweenAndShedIdOrderByDateAsc(startDate, endDate, shedId);
    }

    public void deleteFlock(UUID flockDataID) {
        FlockEntity flockEntity = getFlock(flockDataID);
        flockRepository.deleteById(flockDataID);

        flockCount-=flockEntity.getCount();
        Integer shedId = flockEntity.getShed().getId();
        flockCountMap.put(shedId, flockCountMap.getOrDefault(shedId, 0) - flockEntity.getCount());

        materialStockCalculator.updateMinimumStockQuantity(flockCountMap);
    }

    public FlockEntity updateFlock(UUID flockDataId, Flock flock) {
        if(!flockDataId.equals(flock.getId()))
            throw new MismatchException("Flock ID Mismatch in Put Request");

        FlockEntity flockEntityInDb = getFlock(flockDataId);

        int prevCount = flockEntityInDb.getCount();

        BeanUtils.copyProperties(flock, flockEntityInDb, "createdBy", "createdAt");

        flockEntityInDb.setCount(flock.getAdded() ? flock.getCount() : -flock.getCount());
        flockEntityInDb.setShed(dataService.getShedById(flock.getShedId()));

        FlockEntity flockEntitySaved = flockRepository.save(flockEntityInDb);

        Integer shedId = flock.getShedId();

        if(prevCount > 0) {
            flockCount = flockCount - prevCount;
            flockCountMap.put(shedId, flockCountMap.get(shedId) - prevCount);
        }
        else {
            flockCount = flockCount + Math.abs(prevCount);
            flockCountMap.put(shedId, flockCountMap.get(shedId) + Math.abs(prevCount));
        }

        flockCount += flockEntityInDb.getCount();
        flockCountMap.put(shedId, flockCountMap.get(shedId) + flockEntityInDb.getCount());

        materialStockCalculator.updateMinimumStockQuantity(flockCountMap);

        return flockEntitySaved;
    }

    @PostConstruct
    public FlockCount getFlockCount() {

        if(flockCountMap.isEmpty()) {
            List<FlockEntity> flockEntityList = flockRepository.findAll();
            for (FlockEntity flockEntity : flockEntityList) {
                int shedId = flockEntity.getShed().getId();
                int count = flockEntity.getCount();
                flockCountMap.put(shedId, flockCountMap.getOrDefault(shedId, 0) + count);
                flockCount+=count;
            }
        }
        return new FlockCount(flockCount);
    }

    public FlockCount getFlockShedCount(Integer shedId) {
        return new FlockCount(flockCountMap.get(shedId));
    }
}
