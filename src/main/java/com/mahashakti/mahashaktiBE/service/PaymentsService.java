package com.mahashakti.mahashaktiBE.service;


import com.mahashakti.mahashaktiBE.entities.PaymentsEntity;
import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.PaymentsRepository;
import com.mahashakti.mahashaktiBE.repository.SaleRepository;
import com.mahashakti.mahashaktiBe.model.LatestPayments;
import com.mahashakti.mahashaktiBe.model.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final DataService dataService;
    private final SaleRepository saleRepository;

    public List<PaymentsEntity> getAllPayments(Date startDate, Date endDate) {
        return paymentsRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate);
    }

    public PaymentsEntity postPayment(Payment payment) {
        PaymentsEntity paymentsEntity = new PaymentsEntity();
        BeanUtils.copyProperties(payment, paymentsEntity);

        paymentsEntity.setVendor(dataService.getVendorById(payment.getVendorId()));

        adjustVendorCredits(payment.getVendorId(), payment.getAmount());

        return paymentsRepository.save(paymentsEntity);

    }

    public PaymentsEntity getPaymentById(UUID paymentId) {
        Optional<PaymentsEntity> paymentsEntityOptional = paymentsRepository.findById(paymentId);
        if(paymentsEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Payments Resource Not Found: %s", paymentId));

        return paymentsEntityOptional.get();
    }

    public PaymentsEntity getPaymentBySaleId(UUID saleId) {
        Optional<PaymentsEntity> paymentsEntityOptional = paymentsRepository.findBySaleId(saleId);
        if(paymentsEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Payments Resource Not Found: %s", saleId.toString()));

        return paymentsEntityOptional.get();
    }


    public List<PaymentsEntity> getPaymentByVendorId(Integer vendorId, Date startDate, Date endDate) {
        List<PaymentsEntity> paymentsEntityList = paymentsRepository.findByPaymentDateBetweenAndVendorIdOrderByPaymentDateDesc(startDate, endDate, vendorId);
        if(paymentsEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Payments Resource Not Found with Vendor Id: %d", vendorId));

        return paymentsEntityList;
    }

    public PaymentsEntity putPaymentsPaymentId(UUID paymentId, Payment payment) {

        if(!paymentId.equals(payment.getId()))
            throw new MismatchException("Payment ID Mismatch in Put Request");

        PaymentsEntity paymentsEntityInDb = getPaymentById(paymentId);

        BigDecimal settlementAmount = payment.getAmount().subtract(paymentsEntityInDb.getAmount());
        adjustVendorCredits(paymentsEntityInDb.getVendor().getId(), settlementAmount);

        BeanUtils.copyProperties(payment, paymentsEntityInDb, "createdBy", "createdAt");

        return paymentsRepository.save(paymentsEntityInDb);

    }

    public void deletePaymentsPaymentId(UUID paymentId) {

        PaymentsEntity paymentsEntity = getPaymentById(paymentId);
        adjustVendorCredits(paymentsEntity.getVendor().getId(), paymentsEntity.getAmount().multiply(new BigDecimal(-1)));
        paymentsRepository.deleteById(paymentId);

    }

    public Map<Integer, LatestPayments> getLatestPayments() {
        List<VendorEntity> vendorEntityList = dataService.getVendors();
        Map<Integer, LatestPayments> vendorToLatestPayment = new HashMap<>();
        vendorEntityList.forEach(vendorEntity -> {
            Optional<PaymentsEntity> paymentsEntityOptional = paymentsRepository
                    .findTopByVendorIdOrderByPaymentDateDesc(vendorEntity.getId());
            if(paymentsEntityOptional.isPresent()) {
                LatestPayments latestPayments = new LatestPayments();
                BeanUtils.copyProperties(paymentsEntityOptional.get(), latestPayments);
                vendorToLatestPayment.put(vendorEntity.getId(), latestPayments);
            }
        });

        return vendorToLatestPayment;

    }

    @Transactional
    public List<PaymentsEntity> populatePayments() {

        List<PaymentsEntity> paymentsEntityList  = saleRepository.findByPaidAmountGreaterThan(BigDecimal.ZERO).stream().map(saleEntity -> {
            PaymentsEntity paymentsEntity = new PaymentsEntity();
            paymentsEntity.setSale(saleEntity);
            paymentsEntity.setVendor(saleEntity.getVendor());
            paymentsEntity.setAmount(saleEntity.getPaidAmount());
            paymentsEntity.setRemarks("Populating Payments");
            paymentsEntity.setCreatedBy("Mahashakti");
            paymentsEntity.setPaymentDate(saleEntity.getSaleDate());
            return paymentsEntity;
        }).toList();

        paymentsRepository.saveAll(paymentsEntityList);

        return paymentsEntityList;
    }


    @Transactional
    public void adjustVendorCredits(Integer vendorId, BigDecimal adjustAmount) {

        if (adjustAmount.compareTo(BigDecimal.ZERO) == 0) return;

        List<SaleEntity> sales;

        if (adjustAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Positive amount: Settle sales in ascending order of creation (oldest first)
            sales = saleRepository.findByVendorIdAndPaidOrderByCreatedAtAsc(vendorId, Boolean.FALSE);
        } else {
            // Negative amount: Unsettle sales in descending order of creation (newest first)
            sales = saleRepository.findByVendorIdAndPaidAmountGreaterThanOrderByCreatedAtDesc(vendorId, BigDecimal.ZERO);
        }

        BigDecimal remainingAdjustAmount = adjustAmount.abs();

        List<SaleEntity> updatedSales = new ArrayList<>();

        for (SaleEntity sale : sales) {
            BigDecimal paidAmount = sale.getPaidAmount();
            BigDecimal saleAmount = sale.getAmount();

            if (remainingAdjustAmount.compareTo(BigDecimal.ZERO) <= 0) break; // No adjustment left to apply

            if (adjustAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Settling sales
                if (remainingAdjustAmount.compareTo(saleAmount) >= 0) {
                    // Fully settle this sale
                    sale.setPaidAmount(saleAmount);
                    sale.setPaid(Boolean.TRUE);
                    remainingAdjustAmount = remainingAdjustAmount.subtract(saleAmount);
                } else {
                    // Partially settle this sale
                    sale.setPaidAmount(paidAmount.add(remainingAdjustAmount));
                    sale.setPaid(Boolean.FALSE); // Partially paid
                    remainingAdjustAmount = BigDecimal.ZERO;
                }
            } else {
                // Unsettling sales
                if (remainingAdjustAmount.compareTo(paidAmount) >= 0) {
                    // Fully unsettle this sale
                    sale.setPaidAmount(BigDecimal.ZERO);
                    sale.setPaid(Boolean.FALSE);
                    remainingAdjustAmount = remainingAdjustAmount.subtract(paidAmount);
                } else {
                    // Partially unsettle this sale
                    sale.setPaidAmount(paidAmount.subtract(remainingAdjustAmount));
                    sale.setPaid(Boolean.FALSE); // Partially paid
                    remainingAdjustAmount = BigDecimal.ZERO;
                }
            }
            updatedSales.add(sale);
        }

        if (remainingAdjustAmount.compareTo(BigDecimal.ZERO) == 0) {
            saleRepository.saveAll(updatedSales);
            return;
        }

        throw new IllegalArgumentException("Adjustment amount exceeds applicable sales");

    }

}
