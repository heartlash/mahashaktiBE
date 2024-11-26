package com.mahashakti.mahashaktiBE.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import com.mahashakti.mahashaktiBE.config.MinimumStockDaysProperties;
import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import com.mahashakti.mahashaktiBE.entities.FeedCompositionEntity;
import com.mahashakti.mahashaktiBE.repository.MaterialRepository;
import com.mahashakti.mahashaktiBE.repository.MaterialStockRepository;
import com.mahashakti.mahashaktiBE.service.FeedService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
@Slf4j
public  class MaterialStockCalculator {

    private final MinimumStockDaysProperties minimumStockDaysProperties;
    private final MaterialRepository materialRepository;
    private final MaterialStockRepository materialStockRepository;
    private final FeedService feedService;

    @Transactional
    public void updateMinimumStockQuantity(Map<Integer, Integer> shedToFlock) {


        materialRepository.findAll().forEach(materialEntity -> {

            Optional<MaterialStockEntity> materialStockEntityOptional =
                    materialStockRepository.findById(materialEntity.getId());

            BigDecimal dailyExpectedMaterialConsumption = getDailyExpectedMaterialConsumption(materialEntity.getName(), shedToFlock);

            BigDecimal minQuantity = dailyExpectedMaterialConsumption
                    .multiply(new BigDecimal(minimumStockDaysProperties.getMaterials().get(materialEntity.getName())));

            if (materialStockEntityOptional.isPresent()) {
                materialStockEntityOptional.get().setMinQuantity(minQuantity);
                materialStockRepository.save(materialStockEntityOptional.get());
            }

        });
    }

    public Integer stockLastDay(Integer materialId, Map<Integer, Integer> shedToFlock) {

        Optional<MaterialStockEntity> materialStockEntityOptional = materialStockRepository.findById(materialId);
        if (materialStockEntityOptional.isEmpty()) return 0;
        MaterialStockEntity materialStockEntity = materialStockEntityOptional.get();

        BigDecimal totalStock = materialStockEntity.getQuantity();

        String materialName = materialStockEntity.getMaterial().getName();

        BigDecimal dailyConsumption = getDailyExpectedMaterialConsumption(materialName, shedToFlock);

        if (dailyConsumption.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        BigDecimal stockLastsFor = totalStock.divide(dailyConsumption, RoundingMode.DOWN);
        return stockLastsFor.intValue();
    }


    public BigDecimal getDailyExpectedMaterialConsumption(String materialName,
                                                          Map<Integer, Integer> shedToFlock) {

        BigDecimal dailyExpectedMaterialConsumption = BigDecimal.ZERO;

        for (Integer shedId : shedToFlock.keySet()) {

            BigDecimal feedPerBirdInGrams = feedService.getFeedQuantityShedShedId(shedId).getQuantityPerBird();
            BigDecimal feedPerBirdInTonne = feedPerBirdInGrams.divide(new BigDecimal(1000000));

            BigDecimal totalFeedForBirdInShed = feedPerBirdInTonne.multiply(new BigDecimal(shedToFlock.get(shedId)));

            FeedCompositionEntity feedCompositionEntity = feedService.getFeedCompositionShedShedId(shedId)
                    .stream()
                    .filter(feedComposition -> feedComposition.getMaterial().getName().equals(materialName))
                    .findFirst().get();

            String materialUnit = feedCompositionEntity.getMaterial().getUnit().getName();
            BigDecimal materialRatio = new BigDecimal(0);

            if (materialUnit.equalsIgnoreCase("Kilogram")) {
                materialRatio = feedCompositionEntity.getQuantityPerTonne().divide(BigDecimal.valueOf(1000));
                totalFeedForBirdInShed = totalFeedForBirdInShed.multiply(new BigDecimal(1000));
            } else if (materialUnit.equalsIgnoreCase("Tonne")) {
                materialRatio = feedCompositionEntity.getQuantityPerTonne();
            }

            BigDecimal shedDailyExpectedMaterialConsumption = totalFeedForBirdInShed.multiply(materialRatio);

            dailyExpectedMaterialConsumption = dailyExpectedMaterialConsumption.add(shedDailyExpectedMaterialConsumption);
        }

        return dailyExpectedMaterialConsumption;
    }
}
