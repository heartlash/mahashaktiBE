package com.mahashakti.mahashaktiBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mahashakti.mahashaktiBE.entities.FlockEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface FlockRepository extends JpaRepository<FlockEntity, UUID> {
    List<FlockEntity> findByDateBetweenOrderByDateAsc(Date startDate, Date endDate);
    List<FlockEntity> findByDateBetweenAndShedIdOrderByDateAsc(Date startDate, Date endDate, Integer shedId);
}
