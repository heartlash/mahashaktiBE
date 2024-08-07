package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.ProductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Date;

@Repository
public interface ProductionRepository extends JpaRepository<ProductionEntity, UUID> {
    List<ProductionEntity> findByProductionDateBetween(Date startDate, Date endDate);
    Optional<ProductionEntity> findTopByOrderByProductionDateDesc();
}
