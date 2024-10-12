package com.mahashakti.mahashaktiBE.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Set;


import com.mahashakti.mahashaktiBE.config.ConsumptionProperties;
import com.mahashakti.mahashaktiBE.config.MinimumStockDaysProperties;
import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
@Slf4j
public  class MaterialStockCalculator {

    private final ConsumptionProperties consumptionProperties;
    private final MinimumStockDaysProperties minimumStockDaysProperties;
    private final MaterialRepository materialRepository;
    private final MaterialStockRepository materialStockRepository;

    @Transactional
    public void updateMinimumStockQuantity(Integer currentFlockCount) {

        Set<String> allMaterials = consumptionProperties.getAdult().keySet();

        allMaterials.forEach(material -> {
            Optional<MaterialEntity> materialOptional = materialRepository.findByName(material);

            if (materialOptional.isPresent()) {
                MaterialEntity materialEntity = materialOptional.get();

                Optional<MaterialStockEntity> materialStockEntityOptional =
                        materialStockRepository.findById(materialEntity.getId());

                BigDecimal minQuantity = consumptionProperties.getAdult().get(materialEntity.getName())
                        .multiply(new BigDecimal(currentFlockCount))
                        .multiply(new BigDecimal(minimumStockDaysProperties.getMaterials().get(materialEntity.getName())));

                if (materialStockEntityOptional.isPresent()) {
                    materialStockEntityOptional.get().setMinQuantity(minQuantity);
                    materialStockRepository.save(materialStockEntityOptional.get());
                }
            }
        });
    }

    public Integer stockLastDay(Integer materialId, Integer currentFlockCount) {

        Optional<MaterialStockEntity> materialStockEntityOptional = materialStockRepository.findById(materialId);

        if (materialStockEntityOptional.isPresent()) {

            BigDecimal currentQuantity = materialStockEntityOptional.get().getQuantity();

            BigDecimal perBirdPerDay = consumptionProperties.getAdult().get(materialStockEntityOptional.get().getMaterial().getName());
            if(perBirdPerDay.compareTo(new BigDecimal(0)) == 0) return 0;

            BigDecimal totalDailyConsumption = perBirdPerDay.multiply(new BigDecimal(currentFlockCount));
            BigDecimal daysStockWillLast = totalDailyConsumption.compareTo(new BigDecimal(0)) == 0
                    ? new BigDecimal(0) : currentQuantity.divide(totalDailyConsumption, RoundingMode.DOWN);

            return daysStockWillLast.setScale(0, RoundingMode.DOWN).intValue();
        }
        return 0;


    }

    public BigDecimal getDailyExpectedMaterialConsumption(String materialName, Integer currentFlockCount) {
        BigDecimal materialConsumptionPerBird = consumptionProperties.getAdult().get(materialName);
        return materialConsumptionPerBird.multiply(new BigDecimal(currentFlockCount));
    }
}
