package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Date;

@Repository
public interface MaterialPurchaseRepository extends JpaRepository<MaterialPurchaseEntity, UUID> {

    List<MaterialPurchaseEntity> findByPurchaseDateBetweenOrderByPurchaseDateAsc(Date startDate, Date endDate);
    List<MaterialPurchaseEntity> findByPurchaseDateBetweenAndCreatedBy(Date startDate, Date endDate, String createdBy);
    List<MaterialPurchaseEntity> findByPurchaseDateBetweenAndMaterialId(Date startDate, Date endDate, Integer materialId);
    Optional<MaterialPurchaseEntity> findTopByMaterialIdOrderByPurchaseDateDesc(Integer materialId);
}
