package com.mahashakti.mahashaktiBE.service;

import com.mahashakti.mahashaktiBE.entities.ShedEntity;
import com.mahashakti.mahashaktiBE.entities.FeedQuantityEntity;
import com.mahashakti.mahashaktiBE.entities.FeedCompositionEntity;
import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import com.mahashakti.mahashaktiBE.exception.MismatchException;
import com.mahashakti.mahashaktiBE.exception.ResourceNotFoundException;
import com.mahashakti.mahashaktiBE.repository.FeedCompositionRepository;
import com.mahashakti.mahashaktiBE.repository.FeedQuantityRepository;
import com.mahashakti.mahashaktiBe.model.FeedComposition;
import com.mahashakti.mahashaktiBe.model.FeedQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedQuantityRepository feedQuantityRepository;
    private final FeedCompositionRepository feedCompositionRepository;
    private final DataService dataService;

    public List<FeedQuantityEntity> getFeedQuantity() {
        return feedQuantityRepository.findAll();
    }

    public FeedQuantityEntity getFeedQuantityShedShedId(Integer shedId) {
        Optional<FeedQuantityEntity> optionalFeedQuantityEntity = feedQuantityRepository.findByShedId(shedId);
        if(optionalFeedQuantityEntity.isEmpty())
            throw new ResourceNotFoundException(String.format("Feed Quantity Resource Not Found: %d", shedId));
        return optionalFeedQuantityEntity.get();
    }

    public FeedQuantityEntity getFeedQuantity(Integer id) {
        Optional<FeedQuantityEntity> optionalFeedQuantityEntity = feedQuantityRepository.findById(id);
        if(optionalFeedQuantityEntity.isEmpty())
            throw new ResourceNotFoundException(String.format("Feed Quantity Resource Not Found: %d", id));
        return optionalFeedQuantityEntity.get();
    }
    
    public FeedQuantityEntity postFeedQuantity(FeedQuantity feedQuantity) {
        FeedQuantityEntity feedQuantityEntity = new FeedQuantityEntity();
        BeanUtils.copyProperties(feedQuantity, feedQuantityEntity);

        ShedEntity shedEntity = dataService.getShedById(feedQuantity.getShedId());
        feedQuantityEntity.setShed(shedEntity);

        return feedQuantityRepository.save(feedQuantityEntity);
    }

    public FeedQuantityEntity putFeedQuantity(Integer id, FeedQuantity feedQuantity) {
        if(!id.equals(feedQuantity.getId())) throw new MismatchException("Feed Composition ID Mismatch in Put Request");

        FeedQuantityEntity feedQuantityEntityInDb = getFeedQuantity(id);

        BeanUtils.copyProperties(feedQuantity, feedQuantityEntityInDb);

        if(!feedQuantity.getShedId().equals(feedQuantityEntityInDb.getShed().getId())) {
            ShedEntity shedEntity = dataService.getShedById(feedQuantity.getShedId());
            feedQuantityEntityInDb.setShed(shedEntity);
        }
        return feedQuantityRepository.save(feedQuantityEntityInDb);
    }

    public void deleteFeedQuantity(Integer id) {
        getFeedQuantity(id);
        feedQuantityRepository.deleteById(id);

    }

    public List<FeedCompositionEntity> getFeedComposition() {
        return feedCompositionRepository.findAll();
    }

    public List<FeedCompositionEntity> getFeedCompositionShedShedId(Integer shedId) {
        List<FeedCompositionEntity> feedQuantityEntityList = feedCompositionRepository.findByShedId(shedId);
        if(feedQuantityEntityList.isEmpty())
            throw new ResourceNotFoundException(String.format("Feed Composition Resource Not Found: %d", shedId));
        return feedQuantityEntityList;
    }

    public FeedCompositionEntity getFeedComposition(UUID id) {
        Optional<FeedCompositionEntity> optionalFeedCompositionEntity = feedCompositionRepository.findById(id);
        if(optionalFeedCompositionEntity.isEmpty())
            throw new ResourceNotFoundException(String.format("Feed Composition Resource Not Found: %d", id));
        return optionalFeedCompositionEntity.get();
    }

    public FeedCompositionEntity postFeedComposition(FeedComposition feedComposition) {
        FeedCompositionEntity feedCompositionEntity = new FeedCompositionEntity();

        BeanUtils.copyProperties(feedComposition, feedCompositionEntity);

        MaterialEntity materialEntity = dataService.getMaterialById(feedComposition.getMaterialId());
        feedCompositionEntity.setMaterial(materialEntity);

        ShedEntity shedEntity = dataService.getShedById(feedComposition.getShedId());
        feedCompositionEntity.setShed(shedEntity);

        return feedCompositionRepository.save(feedCompositionEntity);
    }

    public FeedCompositionEntity putFeedComposition(UUID id, FeedComposition feedComposition) {
        if(!id.equals(feedComposition.getId())) throw new MismatchException("Feed Composition ID Mismatch in Put Request");

        FeedCompositionEntity feedCompositionEntityInDb = getFeedComposition(id);

        BeanUtils.copyProperties(feedComposition, feedCompositionEntityInDb);

        if(!feedComposition.getShedId().equals(feedCompositionEntityInDb.getShed().getId())) {
            ShedEntity shedEntity = dataService.getShedById(feedComposition.getShedId());
            feedCompositionEntityInDb.setShed(shedEntity);
        }

        if(!feedComposition.getMaterialId().equals(feedCompositionEntityInDb.getMaterial().getId())) {
            MaterialEntity materialEntity = dataService.getMaterialById(feedComposition.getMaterialId());
            feedCompositionEntityInDb.setMaterial(materialEntity);
        }

        return feedCompositionRepository.save(feedCompositionEntityInDb);
    }

    public void deleteFeedComposition(UUID id) {
        getFeedComposition(id);
        feedCompositionRepository.deleteById(id);
    }
}
