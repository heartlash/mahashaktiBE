package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {

    List<SaleEntity> findBySaleDateBetween(Date startDate, Date endDate);
    List<SaleEntity> findByPaid(Boolean paid);
    List<SaleEntity> findByVendorIdAndPaidOrderByCreatedAtAsc(Integer vendorId, Boolean paid);
    List<SaleEntity> findBySaleDateBetweenOrderBySaleDateAsc(Date startDate, Date endDate);
    List<SaleEntity> findBySaleDateBetweenAndVendorIdOrderBySaleDateAsc(Date startDate, Date endDate, Integer vendorId);
    List<SaleEntity> findBySaleDateBetweenAndVendorIdAndPaidOrderBySaleDateAsc(Date startDate, Date endDate, Integer vendorId, Boolean paid);
    List<SaleEntity> findBySaleDateBetweenAndPaidOrderBySaleDateAsc(Date startDate, Date endDate, Boolean paid);
    Optional<SaleEntity> findTopByOrderBySaleDateDesc();

}
