package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.EggTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EggTypeRepository extends JpaRepository<EggTypeEntity, Integer> {
}
