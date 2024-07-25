package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.service.AnalyticsService;
import com.mahashakti.mahashaktiBe.api.AnalyticsApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class AnalyticsController implements AnalyticsApi {

    private final AnalyticsService analyticsService;

    @Override
    public ResponseEntity<MahashaktiResponse> getAnalyticsProjectedProfits(Date startDate, Date endDate) {
        return AnalyticsApi.super.getAnalyticsProjectedProfits(startDate, endDate);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getMaterialStock() {
        return AnalyticsApi.super.getMaterialStock();
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> getAnalyticsEggStock() {
        analyticsService.test();
        return null;
    }

}
