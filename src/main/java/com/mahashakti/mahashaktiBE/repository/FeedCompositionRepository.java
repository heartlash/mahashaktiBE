package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.FeedCompositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedCompositionRepository extends JpaRepository<FeedCompositionEntity, UUID> {
    List<FeedCompositionEntity> findByShedId(Integer shedId);
    void deleteByShedId(Integer shedId);
}
