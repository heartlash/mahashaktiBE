package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.MaterialRestockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaterialRestockRepository extends JpaRepository<MaterialRestockEntity, UUID> {

    List<MaterialRestockEntity> findByRestockDateBetweenOrderByRestockDateAsc(Date startDate, Date endDate);
    List<MaterialRestockEntity> findByRestockDateBetweenAndMaterialId(Date startDate, Date endDate, Integer materialId);

}