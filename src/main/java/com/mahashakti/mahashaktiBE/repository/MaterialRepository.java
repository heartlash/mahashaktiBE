package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<MaterialEntity, Integer> {

    Optional<MaterialEntity> findByName(String name);
}
