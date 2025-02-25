package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {

    List<SaleEntity> findBySaleDateBetween(Date startDate, Date endDate);
    List<SaleEntity> findByPaid(Boolean paid);
    List<SaleEntity> findByVendorIdAndPaidOrderByCreatedAtAsc(Integer vendorId, Boolean paid);
    List<SaleEntity> findBySaleDateBetweenOrderBySaleDateAsc(Date startDate, Date endDate);
    List<SaleEntity> findBySaleDateBetweenAndVendorIdAndEggTypeIdOrderBySaleDateAsc(Date startDate, Date endDate, Integer vendorId, Integer eggTypeId);
    List<SaleEntity> findBySaleDateBetweenAndVendorIdAndPaidAndEggTypeIdOrderBySaleDateAsc(Date startDate, Date endDate, Integer vendorId, Boolean paid, Integer eggTypeId);
    List<SaleEntity> findBySaleDateBetweenAndPaidAndEggTypeIdOrderBySaleDateAsc(Date startDate, Date endDate, Boolean paid, Integer eggTypeId);
    Optional<SaleEntity> findTopByOrderBySaleDateDesc();
    List<SaleEntity> findByVendorIdAndPaidAmountGreaterThanOrderByCreatedAtDesc(Integer vendorId, BigDecimal amount);
    List<SaleEntity> findByPaidAmountGreaterThan(BigDecimal amount);
    List<SaleEntity> findBySaleDateBetweenAndEggTypeIdOrderBySaleDateAsc(Date startDate, Date endDate, Integer eggTypeId);
}
