package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.constants.EggType;
import com.mahashakti.mahashaktiBE.entities.MaterialPurchaseEntity;
import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import com.mahashakti.mahashaktiBE.entities.ProductionEntity;
import com.mahashakti.mahashaktiBE.entities.SaleEntity;
import com.mahashakti.mahashaktiBE.entities.EggStockEntity;
import com.mahashakti.mahashaktiBE.repository.EggStockRepository;
import com.mahashakti.mahashaktiBE.repository.ProductionRepository;
import com.mahashakti.mahashaktiBE.repository.SaleRepository;
import com.mahashakti.mahashaktiBE.utils.Helper;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;


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
    private final EggStockRepository eggStockRepository;

    private List<PriceRecommendation> cachedPriceRecommendations;
    private LocalDate lastExecutedDate;

    private final Map<String, Integer> currentEggStockCountMap = new HashMap<>();

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
        if(currentEggStockCountMap.isEmpty()) {

            dataService.getEggTypes().forEach(eggTypeEntity -> {
                Integer eggStockToAccount = eggStockRepository.findByEggTypeId(eggTypeEntity.getId()).stream().mapToInt(EggStockEntity::getCount).sum();
                currentEggStockCountMap.put(eggTypeEntity.getName(), eggStockToAccount);
            });

            try {
                List<ProductionEntity> allProductions = productionRepository.findByProductionDateBetweenOrderByProductionDateDesc(
                        new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), new Date());

                Integer gradeAProductionCount = 0;
                Integer gradeBProductionCount = 0;

                for(ProductionEntity productionEntity : allProductions) {

                    gradeAProductionCount += productionEntity.getProducedCount() - productionEntity.getBrokenCount() - productionEntity.getSelfUseCount()
                            - productionEntity.getGiftCount();

                    gradeBProductionCount += productionEntity.getBrokenCount() - productionEntity.getWasteCount();
                }

                List<SaleEntity> allSales = saleRepository.findBySaleDateBetween(
                        new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), new Date());

                Integer gradeASaleCount = 0;
                Integer gradeBSaleCount = 0;

                for(SaleEntity saleEntity : allSales) {
                    if(Objects.equals(saleEntity.getEggType().getName(), EggType.GRADE_B.name()))
                        gradeBSaleCount += saleEntity.getSoldCount();
                    else
                        gradeASaleCount += saleEntity.getSoldCount();
                }

                currentEggStockCountMap.put(EggType.GRADE_A.name(),
                        currentEggStockCountMap.getOrDefault(EggType.GRADE_A.name(), 0) + gradeAProductionCount - gradeASaleCount);
                currentEggStockCountMap.put(EggType.GRADE_B.name(),
                        currentEggStockCountMap.getOrDefault(EggType.GRADE_B.name(), 0) + gradeBProductionCount - gradeBSaleCount);


            } catch (Exception e) {
                log.error("Failed to get egg stock: {}", e.toString());
            }
        }
        return new EggCount(currentEggStockCountMap.get(EggType.GRADE_A.name()), currentEggStockCountMap.get(EggType.GRADE_B.name()));
    }

 /*

    public EggCount getEggStockTally() {
        if(currentEggStockCountMap.isEmpty()) {

            dataService.getEggTypes().forEach(eggTypeEntity -> {
                Integer eggStockToAccount = eggStockRepository.findByEggTypeId(eggTypeEntity.getId()).stream().mapToInt(EggStockEntity::getCount).sum();
                currentEggStockCountMap.put(eggTypeEntity.getName(), eggStockToAccount);
            });

            try {

                Calendar startCalendar = new GregorianCalendar(2025, Calendar.NOVEMBER, 1);
                Date startDate = startCalendar.getTime();

                Date endDate = new Date();
                List<Date> dateRange = generateDates(startDate, endDate);

                log.info("See list of dates: {}", dateRange);

                for(Date currentDate: dateRange) {

                    Integer gradeACountConsideration = eggStockRepository.findByEggTypeIdAndDateBetweenOrderByDateDesc(1,  new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), currentDate).stream().mapToInt(EggStockEntity::getCount).sum();
                    Integer gradeBCountConsideration = eggStockRepository.findByEggTypeIdAndDateBetweenOrderByDateDesc(2,  new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), currentDate).stream().mapToInt(EggStockEntity::getCount).sum();


                    List<ProductionEntity> allProductions = productionRepository.findByProductionDateBetweenOrderByProductionDateDesc(
                            new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), currentDate);

                    Integer gradeAProductionCount = 0;
                    Integer gradeBProductionCount = 0;

                    for (ProductionEntity productionEntity : allProductions) {

                        gradeAProductionCount += productionEntity.getProducedCount() - productionEntity.getBrokenCount() - productionEntity.getSelfUseCount()
                                - productionEntity.getGiftCount();

                        gradeBProductionCount += productionEntity.getBrokenCount() - productionEntity.getWasteCount();
                    }

                    List<SaleEntity> allSales = saleRepository.findBySaleDateBetween(
                            new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), currentDate);

                    Integer gradeASaleCount = 0;
                    Integer gradeBSaleCount = 0;

                    for (SaleEntity saleEntity : allSales) {
                        if (Objects.equals(saleEntity.getEggType().getName(), EggType.GRADE_B.name()))
                            gradeBSaleCount += saleEntity.getSoldCount();
                        else
                            gradeASaleCount += saleEntity.getSoldCount();
                    }

                    currentEggStockCountMap.put(EggType.GRADE_A.name(),
                            currentEggStockCountMap.getOrDefault(EggType.GRADE_A.name(), 0) + gradeAProductionCount - gradeASaleCount);
                    currentEggStockCountMap.put(EggType.GRADE_B.name(),
                            currentEggStockCountMap.getOrDefault(EggType.GRADE_B.name(), 0) + gradeBProductionCount - gradeBSaleCount);

                    Integer one = (gradeAProductionCount - gradeASaleCount + gradeACountConsideration)/210;
                    Integer two = (gradeBProductionCount - gradeBSaleCount + gradeBCountConsideration)/210;

                    log.info("GRADE A Egg Stock on  {}:     {} Cartons", new SimpleDateFormat("MMM dd yyyy").format(currentDate), ((gradeAProductionCount - gradeASaleCount + gradeACountConsideration)/210));
                    log.info("GRADE B Egg Stock on  {}:     {} Cartons", new SimpleDateFormat("MMM dd yyyy").format(currentDate), ((gradeBProductionCount - gradeBSaleCount + gradeBCountConsideration)/210));
                    log.info("Total Egg Stock on  {}:     {} Cartons", new SimpleDateFormat("MMM dd yyyy").format(currentDate), (one + two));

                }


            } catch (Exception e) {
                log.error("Failed to get egg stock: {}", e.toString());
            }
        }
        return new EggCount(currentEggStockCountMap.get(EggType.GRADE_A.name()), currentEggStockCountMap.get(EggType.GRADE_B.name()));
    }

    public static List<Date> generateDates(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1); // Increment the date by one day
        }

        return dates;
    }

*/


    public void incrementEggStockCount(Integer increaseCount, EggType eggType) {
        if(EggType.GRADE_A == eggType)
            currentEggStockCountMap.put(EggType.GRADE_A.name(), currentEggStockCountMap.get(EggType.GRADE_A.name()) + increaseCount);
        else
            currentEggStockCountMap.put(EggType.GRADE_B.name(), currentEggStockCountMap.get(EggType.GRADE_B.name()) + increaseCount);
    }

    public void decrementEggStockCount(Integer decreaseCount, EggType eggType) {
        if(EggType.GRADE_A == eggType)
            currentEggStockCountMap.put(EggType.GRADE_A.name(), currentEggStockCountMap.get(EggType.GRADE_A.name()) - decreaseCount);
        else
            currentEggStockCountMap.put(EggType.GRADE_B.name(), currentEggStockCountMap.get(EggType.GRADE_B.name()) - decreaseCount);

    }

    public List<PriceRecommendation> getPriceRecommendation() {

        if (lastExecutedDate != null && lastExecutedDate.equals(LocalDate.now()) && cachedPriceRecommendations != null)
            return cachedPriceRecommendations;

        List<PriceRecommendation> priceRecommendationList = new ArrayList<>();

        List<String> urls = List.of("https://todayeggrate.in/andhra-pradesh-egg-rate", "https://todayeggrate.in/barwala-egg-rate");

        for (String url : urls) {
            PriceRecommendation priceRecommendation = new PriceRecommendation();
            try {
                Document doc = Jsoup.connect(url).get();
                Element priceElement = doc.select("table tbody tr:nth-child(1) td:nth-child(2)").first();
                String region = Helper.capitalizeFirstLetter(url.substring(url.lastIndexOf("/") + 1).replace("-", " ")
                        .replace("egg rate", ""));

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
