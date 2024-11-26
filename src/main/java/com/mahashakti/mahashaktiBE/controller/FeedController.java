package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.entities.FeedCompositionEntity;
import com.mahashakti.mahashaktiBE.entities.FeedQuantityEntity;
import com.mahashakti.mahashaktiBE.service.FeedService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.FeedApi;
import com.mahashakti.mahashaktiBe.model.FeedComposition;
import com.mahashakti.mahashaktiBe.model.FeedQuantity;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FeedController implements FeedApi {

    private final FeedService feedService;

    @Override
    public ResponseEntity<MahashaktiResponse> getFeedQuantity() {

        List<FeedQuantityEntity> feedQuantityEntityList = feedService.getFeedQuantity();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Feed Quantity Fetched", "SUCCESS", feedQuantityEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getFeedQuantityShedShedId(Integer shedId) {
        FeedQuantityEntity feedQuantityEntity = feedService.getFeedQuantityShedShedId(shedId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Feed Quantity Fetched", "SUCCESS", feedQuantityEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postFeedQuantity(FeedQuantity feedQuantity) {
        FeedQuantityEntity feedQuantityEntity = feedService.postFeedQuantity(feedQuantity);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Feed Quantity  ADDED", "SUCCESS", feedQuantityEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putFeedQuantity(Integer id, FeedQuantity feedQuantity) {
        FeedQuantityEntity feedQuantityEntity = feedService.putFeedQuantity(id, feedQuantity);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Feed Quantity UPDATED", "SUCCESS", feedQuantityEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteFeedQuantity(Integer id) {
        feedService.deleteFeedQuantity(id);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Feed Quantity DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getFeedComposition() {
        List<FeedCompositionEntity> feedCompositionEntityList = feedService.getFeedComposition();
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Feed Composition Fetched", "SUCCESS", feedCompositionEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> getFeedCompositionShedShedId(Integer shedId) {
        List<FeedCompositionEntity> feedCompositionEntityList = feedService.getFeedCompositionShedShedId(shedId);
        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE200", "Feed Composition Fetched", "SUCCESS", feedCompositionEntityList);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> postFeedComposition(FeedComposition feedComposition) {
        FeedCompositionEntity feedCompositionEntity = feedService.postFeedComposition(feedComposition);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE201", "Feed Composition ADDED", "SUCCESS", feedCompositionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> putFeedComposition(UUID id, FeedComposition feedComposition) {
        FeedCompositionEntity feedCompositionEntity = feedService.putFeedComposition(id, feedComposition);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Feed Composition UPDATED", "SUCCESS", feedCompositionEntity);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<MahashaktiResponse> deleteFeedComposition(UUID id) {
        feedService.deleteFeedComposition(id);

        MahashaktiResponse mahashaktiResponse
                = Helper.createResponse("MSBE202", "Feed Composition DELETED", "SUCCESS", null);

        return new ResponseEntity<>(mahashaktiResponse, HttpStatus.OK);
    }
}
