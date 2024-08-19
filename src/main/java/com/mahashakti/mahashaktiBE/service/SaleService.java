package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.SaleRepository;
import com.mahashakti.mahashaktiBe.model.Sale;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository saleRepository;
    private final DataService dataService;
    private final AnalyticsService analyticsService;

    public List<SaleEntity> getAllSale(Date startDate, Date endDate, Integer vendorId, Boolean paid) {
        if(Objects.isNull(vendorId) && Objects.isNull(paid)) return saleRepository.findBySaleDateBetween(startDate, endDate);
        if(!Objects.isNull(vendorId) && !Objects.isNull(paid))
            return saleRepository.findBySaleDateBetweenAndVendorIdAndPaid(startDate, endDate, vendorId, paid);

        if(Objects.isNull(vendorId)) return saleRepository.findBySaleDateBetweenAndPaid(startDate, endDate, paid);
        return saleRepository.findBySaleDateBetweenAndVendorId(startDate, endDate, vendorId);
    }

    @Transactional
    public List<SaleEntity> postSale(List<Sale> sales) {
        AtomicReference<Integer> totalSoldCount = new AtomicReference<>(0);
        List<SaleEntity> saleEntityList = sales.stream().map(sale -> {
            SaleEntity saleEntity = new SaleEntity();
            BeanUtils.copyProperties(sale, saleEntity);
            saleEntity.setVendor(dataService.getVendorById(sale.getVendorId()));
            totalSoldCount.updateAndGet(v -> v + saleEntity.getSoldCount());
            return saleEntity;
        }).toList();
        analyticsService.decrementEggStockCount(totalSoldCount.get());
        return saleRepository.saveAll(saleEntityList);
    }

    public SaleEntity getSaleById(UUID saleId) {
        Optional<SaleEntity> saleEntityOptional = saleRepository.findById(saleId);
        if(saleEntityOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Sale Resource Not Found %s", saleId.toString()));

        return saleEntityOptional.get();
    }

    public List<SaleEntity> getSaleLatest() {
        Optional<SaleEntity> saleEntityOptionalLatest = saleRepository.findTopByOrderBySaleDateDesc();
        if(saleEntityOptionalLatest.isEmpty())
            throw new ResourceNotFoundException("Latest Sale Resource Not Found %s");

        return getAllSale(saleEntityOptionalLatest.get().getSaleDate(), saleEntityOptionalLatest.get().getSaleDate(), null, null);
    }


    public SaleEntity putSaleById(UUID saleId, Sale sale) {

        if(!saleId.equals(sale.getId()))
            throw new MismatchException("Sale Resource ID Mismatch in Put Request");

        SaleEntity saleEntityInDb = getSaleById(saleId);
        Integer soldCountBefore = saleEntityInDb.getSoldCount();

        BeanUtils.copyProperties(sale, saleEntityInDb, "createdBy", "createdAt");

        if(!sale.getVendorId().equals(saleEntityInDb.getVendor().getId()))
            saleEntityInDb.setVendor(dataService.getVendorById(sale.getVendorId()));

        SaleEntity saleEntitySaved = saleRepository.save(saleEntityInDb);

        if(!saleEntitySaved.getSoldCount().equals(soldCountBefore)) {
            analyticsService.incrementEggStockCount(soldCountBefore);
            analyticsService.decrementEggStockCount(saleEntitySaved.getSoldCount());
        }
        return saleEntitySaved;
    }

    public void deleteSaleById(UUID saleId) {
        SaleEntity saleEntity = getSaleById(saleId);
        saleRepository.deleteById(saleId);
        analyticsService.incrementEggStockCount(saleEntity.getSoldCount());
    }

}
