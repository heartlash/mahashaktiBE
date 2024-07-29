package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.MaterialInStock;
import com.mahashakti.mahashaktiBe.model.ProjectedProfits;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExpenseService expenseService;
    private final SaleService saleService;

    private final DataService dataService;


    public ProjectedProfits getAnalyticsProjectedProfits(Date startDate, Date endDate) {

        // get all expense from material purchase
        // get all expense from operation expenses
        // get all profits and credit data from sales;
        // return sale - expenses

        BigDecimal materialPurchaseExpenseAmount = expenseService.getAllMaterialPurchaseExpenses(startDate, endDate, null)
                        .stream().map(MaterialPurchaseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal operationalExpenseAmount = expenseService.getAllOperationalExpenses(startDate, endDate, null)
                .stream().map(OperationalExpenseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saleAmount = BigDecimal.ZERO;
        BigDecimal creditAmount = BigDecimal.ZERO;

        List<SaleEntity> sales = saleService.getAllSale(startDate, endDate, null, null);

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
        return dataService.getMaterials().stream().map(materialEntity -> {
            MaterialInStock materialInStock = new MaterialInStock();
            materialInStock.setMaterialId(materialEntity.getId());
            materialInStock.setMaterial(materialEntity.getName());
            materialInStock.setUnit(materialEntity.getUnit().getName());
            materialInStock.setUnitId(materialEntity.getUnit().getId());
            materialInStock.setQuantity(materialEntity.getSku());
            return materialInStock;
        }).toList();
    }

    public ResponseEntity<MahashaktiResponse> getAnalyticsEggStock() {
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Sale FETCHED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

}
