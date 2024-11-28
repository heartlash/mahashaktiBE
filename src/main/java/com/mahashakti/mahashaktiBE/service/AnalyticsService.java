package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.entities.ProductionEntity;
import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.repository.ProductionRepository;
import com.mahashakti.mahashaktiBE.repository.SaleRepository;
import com.mahashakti.mahashaktiBE.utils.MaterialStockCalculator;
import com.mahashakti.mahashaktiBe.model.EggCount;
import com.mahashakti.mahashaktiBe.model.MaterialInStock;
import com.mahashakti.mahashaktiBe.model.PriceRecommendation;
import com.mahashakti.mahashaktiBe.model.ProjectedProfits;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final MaterialPurchaseService materialPurchaseService;
    private final OperationalExpenseService operationalExpenseService;
    private final SaleRepository saleRepository;
    private final ProductionRepository productionRepository;
    private final DataService dataService;
    private final FlockService flockService;
    private final MaterialStockCalculator materialStockCalculator;

    private List<PriceRecommendation> cachedPriceRecommendations;
    private LocalDate lastExecutedDate;

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
                creditAmount = creditAmount.add(saleEntity.getAmount().subtract(saleEntity.getPaidAmount()));
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
            materialInStock.setMinQuantity(materialStockEntity.getMinQuantity());

            materialInStock.setLowStock(materialStockEntity.getMinQuantity().compareTo(materialStockEntity.getQuantity()) < 0);

            materialInStock.setExpectedDailyConsumption(
                    materialStockCalculator.getDailyExpectedMaterialConsumption(
                            materialStockEntity.getMaterial().getName(), flockService.getFlockCountMap()));

            materialInStock.setWouldLastFor(materialStockCalculator.stockLastDay(materialStockEntity.getMaterialId(),  flockService.getFlockCountMap()));
            materialInStock.setLastRestockDate(materialStockEntity.getLastRestockDate());
            materialInStock.setLastRestockQuantity(materialStockEntity.getLastRestockQuantity());
            return materialInStock;
        }).toList();
    }

    @PostConstruct
    public EggCount getAnalyticsEggStock() {
        if(currentEggStockCount <= 0) {
            try {
                Integer saleableProductionCount = productionRepository.findByProductionDateBetweenOrderByProductionDateDesc(
                                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), new Date())
                        .stream()
                        .map(ProductionEntity::getSaleableCount)
                        .reduce(0, Integer::sum);

                Integer soldCount = saleRepository.findBySaleDateBetween(
                                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), new Date())
                        .stream().mapToInt(SaleEntity::getSoldCount).sum();

                currentEggStockCount = saleableProductionCount - soldCount;
            } catch (Exception e) {
                log.error("Failed to get egg stock: {}", e.toString());
            }
        }
        return new EggCount(currentEggStockCount);
    }

    public void incrementEggStockCount(Integer increaseCount) {
        currentEggStockCount+=increaseCount;
    }

    public void decrementEggStockCount(Integer decreaseCount) {
        currentEggStockCount-=decreaseCount;
    }

    public List<PriceRecommendation> getPriceRecommendation() {

        if (lastExecutedDate != null && lastExecutedDate.equals(LocalDate.now()) && cachedPriceRecommendations != null)
            return cachedPriceRecommendations;

        List<PriceRecommendation> priceRecommendationList = new ArrayList<>();

        List<String> urls = List.of("https://eggrate.in/s/Andhra_Pradesh", "https://eggrate.in/z/Barwala");

        for (String url : urls) {
            PriceRecommendation priceRecommendation = new PriceRecommendation();
            try {
                Document doc = Jsoup.connect(url).get();
                Element priceElement = doc.select("table.table.table-striped.border tbody tr:nth-child(1) td:nth-child(2)").first();
                String region = url.substring(url.lastIndexOf("/") + 1).replace("_", " ");

                if (priceElement != null) {
                    priceRecommendation.setRegion(region);
                    BigDecimal price = new BigDecimal(priceElement.text().substring(1));
                    priceRecommendation.setEggPrice(price);
                    priceRecommendation.setCartonPrice(price.multiply(new BigDecimal(210)));
                    priceRecommendation.setCartonPriceWithTransportation(price.add(new BigDecimal(1)).multiply(new BigDecimal(210)));
                    priceRecommendation.setTransportationCharge(new BigDecimal(1));
                    priceRecommendation.setPremiumCharge(new BigDecimal("0.4"));
                    priceRecommendation.setRecommendedEggPrice(priceRecommendation.getEggPrice()
                            .add(priceRecommendation.getTransportationCharge())
                            .add(priceRecommendation.getPremiumCharge()));
                    priceRecommendation.setRecommendedCartonPrice(priceRecommendation.getRecommendedEggPrice()
                            .multiply(new BigDecimal(210))
                    );
                    priceRecommendationList.add(priceRecommendation);
                }
            } catch (Exception e) {
                log.error("Failed to scrap site for egg price: {}", e.toString());

            }
        }

        cachedPriceRecommendations = priceRecommendationList;
        lastExecutedDate = LocalDate.now();

        return priceRecommendationList;

    }
}
