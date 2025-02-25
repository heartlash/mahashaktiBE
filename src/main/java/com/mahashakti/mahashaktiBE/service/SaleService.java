package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.constants.EggType;
import com.mahashakti.mahashaktiBE.entities.PaymentsEntity;
import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.exception.InvalidDataStateException;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.PaymentsRepository;
import com.mahashakti.mahashaktiBE.repository.SaleRepository;
import com.mahashakti.mahashaktiBe.model.Sale;
import com.mahashakti.mahashaktiBe.model.SaleCredit;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;


@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository saleRepository;
    private final DataService dataService;
    private final AnalyticsService analyticsService;
    private final PaymentsRepository paymentsRepository;

    public List<SaleEntity> getAllSale(Date startDate, Date endDate, Integer vendorId, Boolean paid, Integer eggTypeId) {
        if(Objects.isNull(vendorId) && Objects.isNull(paid)) {
            if(Objects.isNull(eggTypeId))
                return saleRepository.findBySaleDateBetweenOrderBySaleDateAsc(startDate, endDate);
            else
                return saleRepository.findBySaleDateBetweenAndEggTypeIdOrderBySaleDateAsc(startDate, endDate, eggTypeId);
        }
        if(!Objects.isNull(vendorId) && !Objects.isNull(paid))
            return saleRepository.findBySaleDateBetweenAndVendorIdAndPaidAndEggTypeIdOrderBySaleDateAsc(startDate, endDate, vendorId, paid, eggTypeId);

        if(Objects.isNull(vendorId)) return saleRepository.findBySaleDateBetweenAndPaidAndEggTypeIdOrderBySaleDateAsc(startDate, endDate, paid, eggTypeId);
        return saleRepository.findBySaleDateBetweenAndVendorIdAndEggTypeIdOrderBySaleDateAsc(startDate, endDate, vendorId, eggTypeId);
    }

    @Transactional
    public SaleEntity postSale(Sale sale) {

        SaleEntity saleEntity = new SaleEntity();
        BeanUtils.copyProperties(sale, saleEntity);
        saleEntity.setVendor(dataService.getVendorById(sale.getVendorId()));
        saleEntity.setEggType(dataService.getEggTypeById(sale.getEggTypeId()));
        saleEntity.setPaid(sale.getAmount().compareTo(sale.getPaidAmount()) > 0 ? Boolean.FALSE : Boolean.TRUE);

        saleEntity = saleRepository.save(saleEntity);
        analyticsService.decrementEggStockCount(saleEntity.getSoldCount(), EggType.valueOf(saleEntity.getEggType().getName()));

        if(saleEntity.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            createRelatedPayment(saleEntity);
        }

        return saleEntity;
    }

    public SaleEntity getSaleById(UUID saleId) {
        Optional<SaleEntity> saleEntityOptional = saleRepository.findById(saleId);
        if(saleEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Sale Resource Not Found %s", saleId));

        return saleEntityOptional.get();
    }

    public List<SaleEntity> getSaleLatest() {
        Optional<SaleEntity> saleEntityOptionalLatest = saleRepository.findTopByOrderBySaleDateDesc();
        if(saleEntityOptionalLatest.isEmpty())
            throw new ResourceNotFoundException("Latest Sale Resource Not Found %s");

        return getAllSale(saleEntityOptionalLatest.get().getSaleDate(), saleEntityOptionalLatest.get().getSaleDate(), null, null, null);
    }


    @Transactional
    public SaleEntity putSaleById(UUID saleId, Sale sale) {

        if(!saleId.equals(sale.getId()))
            throw new MismatchException("Sale Resource ID Mismatch in Put Request");

        SaleEntity saleEntityInDb = getSaleById(saleId);

        String originalPaymentRemarks = saleEntityInDb.getPaymentRemarks();
        BigDecimal originalPaidAmount = saleEntityInDb.getPaidAmount();
        Integer originalVendorId = saleEntityInDb.getVendor().getId();

        Integer soldCountBefore = saleEntityInDb.getSoldCount();

        BeanUtils.copyProperties(sale, saleEntityInDb, "createdBy", "createdAt");
        saleEntityInDb.setEggType(dataService.getEggTypeById(sale.getEggTypeId()));
        saleEntityInDb.setPaid(sale.getAmount().compareTo(sale.getPaidAmount()) > 0 ? Boolean.FALSE : Boolean.TRUE);

        if(!sale.getVendorId().equals(originalVendorId))
            saleEntityInDb.setVendor(dataService.getVendorById(sale.getVendorId()));

        SaleEntity saleEntitySaved = saleRepository.save(saleEntityInDb);

        if(!saleEntitySaved.getSoldCount().equals(soldCountBefore)) {
            analyticsService.incrementEggStockCount(soldCountBefore, EggType.valueOf((saleEntitySaved.getEggType().getName())));
            analyticsService.decrementEggStockCount(saleEntitySaved.getSoldCount(), EggType.valueOf(saleEntitySaved.getEggType().getName()));
        }

        // Update payments too, if related data is changed
        if(!sale.getPaidAmount().equals(originalPaidAmount)
                || !sale.getPaymentRemarks().equals(originalPaymentRemarks)
                || !sale.getVendorId().equals(originalVendorId)) {
            updateRelatedPayment(saleEntitySaved);
        }

        return saleEntitySaved;
    }

    @Transactional
    public void deleteSaleById(UUID saleId) {

        SaleEntity saleEntity = getSaleById(saleId);

        if(saleEntity.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            Optional<PaymentsEntity> paymentsEntityInDbOptional = paymentsRepository.findBySaleId(saleId);
            if(paymentsEntityInDbOptional.isPresent()) {
                paymentsRepository.deleteBySaleId(saleId);
            }
        }

        saleRepository.deleteById(saleId);
        analyticsService.incrementEggStockCount(saleEntity.getSoldCount(), EggType.valueOf(saleEntity.getEggType().getName()));

    }

    public List<SaleCredit> getCredits() {

        List<SaleEntity> allCreditSales = saleRepository.findByPaid(Boolean.FALSE);
        if(allCreditSales.isEmpty()) return new ArrayList<>();

        HashMap<Integer, SaleCredit> creditAmountsByVendor = new HashMap<>();

        for (SaleEntity sale : allCreditSales) {
            Integer vendorId = sale.getVendor().getId();
            String vendorName = sale.getVendor().getName();
            BigDecimal amount = sale.getAmount().subtract(sale.getPaidAmount());

            creditAmountsByVendor.merge(
                    vendorId,
                    new SaleCredit(amount, vendorId, vendorName),
                    (existing, toMerge) -> new SaleCredit(existing.getAmount().add(toMerge.getAmount()), vendorId, vendorName )
            );
        }
        return new ArrayList<>(creditAmountsByVendor.values());
    }

    private void updateRelatedPayment(SaleEntity saleEntity) {

        Optional<PaymentsEntity> paymentsEntityInDbOptional = paymentsRepository.findBySaleId(saleEntity.getId());

        if(paymentsEntityInDbOptional.isPresent()) {

            PaymentsEntity paymentsEntityInDb = paymentsEntityInDbOptional.get();

            paymentsEntityInDb.setVendor(saleEntity.getVendor());
            paymentsEntityInDb.setRemarks(saleEntity.getPaymentRemarks());
            paymentsEntityInDb.setAmount(saleEntity.getPaidAmount());
            paymentsEntityInDb.setUpdatedBy(saleEntity.getUpdatedBy());

            paymentsRepository.save(paymentsEntityInDb);
        } else {

            PaymentsEntity paymentsEntity = new PaymentsEntity();

            BeanUtils.copyProperties(saleEntity, paymentsEntity, "updatedBy");

            paymentsEntity.setSale(saleEntity);
            paymentsEntity.setPaymentDate(new Date());
            paymentsEntity.setRemarks(saleEntity.getPaymentRemarks());
            paymentsEntity.setAmount(saleEntity.getPaidAmount());
            paymentsEntity.setCreatedBy(saleEntity.getUpdatedBy());

            paymentsRepository.save(paymentsEntity);
        }
    }

    private void createRelatedPayment(SaleEntity saleEntity) {

        PaymentsEntity paymentsEntity = new PaymentsEntity();

        BeanUtils.copyProperties(saleEntity, paymentsEntity);

        paymentsEntity.setSale(saleEntity);
        paymentsEntity.setPaymentDate(saleEntity.getSaleDate());
        paymentsEntity.setRemarks(saleEntity.getPaymentRemarks());
        paymentsEntity.setAmount(saleEntity.getPaidAmount());
        paymentsEntity.setCreatedBy(saleEntity.getCreatedBy());

        paymentsRepository.save(paymentsEntity);
    }

}
