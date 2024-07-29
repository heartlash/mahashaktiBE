package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.UUID;
import java.util.List;

public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {

    List<SaleEntity> findBySaleDateBetween(Date startDate, Date endDate);
    List<SaleEntity> findBySaleDateBetweenAndVendorId(Date startDate, Date endDate, Integer vendorId);
    List<SaleEntity> findBySaleDateBetweenAndVendorIdAndPaid(Date startDate, Date endDate, Integer vendorId, Boolean paid);
    List<SaleEntity> findBySaleDateBetweenAndPaid(Date startDate, Date endDate, Boolean paid);

}
