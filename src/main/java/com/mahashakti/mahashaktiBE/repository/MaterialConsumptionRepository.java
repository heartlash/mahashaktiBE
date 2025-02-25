package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.MaterialConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Date;

@Repository
public interface MaterialConsumptionRepository extends JpaRepository<MaterialConsumptionEntity, UUID> {
    List<MaterialConsumptionEntity> findByConsumptionDateBetweenOrderByConsumptionDateAsc(Date startDate, Date endDate);
    List<MaterialConsumptionEntity> findByConsumptionDateBetweenAndMaterialId(Date startDate, Date endDate, Integer materialId);
    List<MaterialConsumptionEntity> getByConsumptionDateAndShedId(Date consumptionDate, Integer shedId);
}
