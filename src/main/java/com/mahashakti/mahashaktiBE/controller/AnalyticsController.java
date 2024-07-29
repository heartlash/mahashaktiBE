package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.service.AnalyticsService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.AnalyticsApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import com.mahashakti.mahashaktiBe.model.MaterialInStock;
import com.mahashakti.mahashaktiBe.model.ProjectedProfits;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnalyticsController implements AnalyticsApi {

    private final AnalyticsService analyticsService;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<MahashaktiResponse> getAnalyticsProjectedProfits(Date startDate, Date endDate) {
        ProjectedProfits projectedProfits = analyticsService.getAnalyticsProjectedProfits(startDate, endDate);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Projected Profits FETCHED", "SUCCESS", projectedProfits);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> getMaterialStock() {

        List<MaterialInStock> materialInStockList = analyticsService.getMaterialInStock();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Material Stock FETCHED", "SUCCESS", materialInStockList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> getAnalyticsEggStock() {
        //analyticsService.test();
        return null;
    }

}
