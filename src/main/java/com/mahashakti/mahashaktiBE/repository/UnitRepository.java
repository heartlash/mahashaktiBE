package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Integer> {
}
