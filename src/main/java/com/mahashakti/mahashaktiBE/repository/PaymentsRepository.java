package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.PaymentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, UUID> {

    List<PaymentsEntity> findByPaymentDateBetweenOrderByPaymentDateDesc(Date startDate, Date endDate);
    List<PaymentsEntity> findByPaymentDateBetweenAndVendorIdOrderByPaymentDateDesc(Date startDate, Date endDate, Integer vendorId);
    Optional<PaymentsEntity> findTopByVendorIdOrderByPaymentDateDesc(Integer vendorId);
    void deleteBySaleId(UUID saleId);
    Optional<PaymentsEntity> findBySaleId(UUID saleId);
}
