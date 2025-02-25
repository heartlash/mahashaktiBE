package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.FeedQuantityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedQuantityRepository extends JpaRepository<FeedQuantityEntity, Integer> {
    Optional<FeedQuantityEntity> findByShedId(Integer shedId);
    void deleteByShedId(Integer shedId);
}
