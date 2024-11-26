package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.ShedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShedsRepository extends JpaRepository<ShedEntity, Integer> {
}
