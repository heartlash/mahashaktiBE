package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.EggStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EggStockRepository extends JpaRepository<EggStockEntity, UUID> {
    List<EggStockEntity> findByEggTypeId(Integer eggTypeId);

}
