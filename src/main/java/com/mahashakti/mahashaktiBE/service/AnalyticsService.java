package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.entities.ProductionEntity;
import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.repository.ProductionRepository;
import com.mahashakti.mahashaktiBE.repository.SaleRepository;
import com.mahashakti.mahashaktiBe.model.EggCount;
import com.mahashakti.mahashaktiBe.model.MaterialInStock;
import com.mahashakti.mahashaktiBe.model.ProjectedProfits;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final MaterialPurchaseService materialPurchaseService;
    private final OperationalExpenseService operationalExpenseService;
    private final SaleRepository saleRepository;
    private final ProductionRepository productionRepository;
    private final DataService dataService;


    private Integer currentEggStockCount = 0;

    public ProjectedProfits getAnalyticsProjectedProfits(Date startDate, Date endDate) {

        // get all expense from material purchase
        // get all expense from operation expenses
        // get all profits and credit data from sales;
        // return sale - expenses

        BigDecimal materialPurchaseExpenseAmount = materialPurchaseService.getAllMaterialPurchases(startDate, endDate, null)
                        .stream().map(MaterialPurchaseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal operationalExpenseAmount = operationalExpenseService.getAllOperationalExpenses(startDate, endDate, null)
                .stream().map(OperationalExpenseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saleAmount = BigDecimal.ZERO;
        BigDecimal creditAmount = BigDecimal.ZERO;

        List<SaleEntity> sales = saleRepository.findBySaleDateBetween(startDate, endDate);

        for (SaleEntity saleEntity : sales) {
            if (!saleEntity.getPaid())
                creditAmount = creditAmount.add(saleEntity.getAmount());
            saleAmount = saleAmount.add(saleEntity.getAmount());
        }

        ProjectedProfits projectedProfits = new ProjectedProfits();
        projectedProfits.setProfit(saleAmount.subtract(materialPurchaseExpenseAmount.add(operationalExpenseAmount)));
        projectedProfits.setCredits(creditAmount);
        projectedProfits.setSaleAmount(saleAmount);
        projectedProfits.setMaterialPurchaseExpenses(materialPurchaseExpenseAmount);
        projectedProfits.setOperationalExpenses(operationalExpenseAmount);

        return projectedProfits;


    }

    public List<MaterialInStock> getMaterialInStock() {
        return dataService.getMaterialStock().stream().map(materialStockEntity -> {
            MaterialInStock materialInStock = new MaterialInStock();
            materialInStock.setMaterialId(materialStockEntity.getMaterialId());
            materialInStock.setMaterial(materialStockEntity.getMaterial().getName());
            materialInStock.setUnit(materialStockEntity.getMaterial().getUnit().getName());
            materialInStock.setUnitId(materialStockEntity.getMaterial().getUnit().getId());
            materialInStock.setQuantity(materialStockEntity.getQuantity());
            materialInStock.setLastPurchaseDate(materialStockEntity.getLastPurchaseDate());
            return materialInStock;
        }).toList();
    }

    @PostConstruct
    public EggCount getAnalyticsEggStock() {
        if(currentEggStockCount <= 0) {
            Integer saleableProductionCount = productionRepository.findByProductionDateBetweenOrderByProductionDateAsc(
                    new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), new Date())
                    .stream()
                    .map(ProductionEntity::getSaleableCount)
                    .reduce(0, Integer::sum);

            Integer soldCount = saleRepository.findBySaleDateBetween(
                            new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), new Date())
                    .stream().mapToInt(SaleEntity::getSoldCount).sum();

            currentEggStockCount = saleableProductionCount - soldCount;
        }
        return new EggCount(currentEggStockCount);
    }

    public void incrementEggStockCount(Integer increaseCount) {
        currentEggStockCount+=increaseCount;
    }

    public void decrementEggStockCount(Integer decreaseCount) {
        currentEggStockCount-=decreaseCount;
    }

}
