package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBe.api.AnalyticsApi;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import org.springframework.http.ResponseEntity;

public class AnalyticsController implements AnalyticsApi {

    @Override
    public ResponseEntity<MahashaktiResponse> getAnalyticsEggStock() {
        return null;
    }
}
