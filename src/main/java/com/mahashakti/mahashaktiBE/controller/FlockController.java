package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.FlockEntity;
import com.mahashakti.mahashaktiBE.service.FlockService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.FlockApi;
import com.mahashakti.mahashaktiBe.model.Flock;
import com.mahashakti.mahashaktiBe.model.FlockCount;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class FlockController implements FlockApi {

    private final FlockService flockService;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> deleteFlockFlockDataId(UUID flockDataId) {

        flockService.deleteFlock(flockDataId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Flock DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

    @Override
    public ResponseEntity<MahashaktiResponse> getFlock() {
        List<FlockEntity> flockEntityList = flockService.getFlocks();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Flock FETCHED", "SUCCESS", flockEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> getFlockCount() {
        FlockCount flockCount = flockService.getFlockCount();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Flock Count FETCHED", "SUCCESS", flockCount);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

    @Override
    public ResponseEntity<MahashaktiResponse> getFlockFlockDataId(UUID flockDataId) {
        FlockEntity flockEntity = flockService.getFlock(flockDataId);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Flock FETCHED", "SUCCESS", flockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> postFlock(Flock flock) {
        FlockEntity flockEntity = flockService.addFlock(flock);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Flock ADDED", "SUCCESS", flockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_SUPERVISOR')")
    public ResponseEntity<MahashaktiResponse> putFlockFlockDataId(UUID flockDataId, Flock flock) {
        FlockEntity flockEntity = flockService.updateFlock(flockDataId, flock);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Flock UPDATED", "SUCCESS", flockEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);    }
}
