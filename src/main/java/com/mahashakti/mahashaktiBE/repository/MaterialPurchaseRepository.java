package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
import java.util.Date;

public interface MaterialPurchaseRepository extends JpaRepository<MaterialPurchaseEntity, UUID> {

    List<MaterialPurchaseEntity> findByPurchaseDateBetweenOrderByPurchaseDateAsc(Date startDate, Date endDate);
    List<MaterialPurchaseEntity> findByPurchaseDateBetweenAndCreatedBy(Date startDate, Date endDate, String createdBy);
    List<MaterialPurchaseEntity> findByPurchaseDateBetweenAndMaterialId(Date startDate, Date endDate, Integer materialId);
}
