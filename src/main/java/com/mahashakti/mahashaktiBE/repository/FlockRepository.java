package com.mahashakti.mahashaktiBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mahashakti.mahashaktiBE.entities.FlockEntity;

import java.util.UUID;

public interface FlockRepository extends JpaRepository<FlockEntity, UUID> {
}
