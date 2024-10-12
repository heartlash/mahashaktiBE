package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.MaterialConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
import java.util.Date;


public interface MaterialConsumptionRepository extends JpaRepository<MaterialConsumptionEntity, UUID> {
    List<MaterialConsumptionEntity> findByConsumptionDateBetweenOrderByConsumptionDateAsc(Date startDate, Date endDate);
    List<MaterialConsumptionEntity> findByConsumptionDateBetweenAndMaterialId(Date startDate, Date endDate, Integer materialId);
    List<MaterialConsumptionEntity> getByConsumptionDate(Date consumptionDate);
}
