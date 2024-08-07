package com.mahashakti.mahashaktiBE.repository;


import com.mahashakti.mahashaktiBE.entities.MaterialStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MaterialStockRepository extends JpaRepository<MaterialStockEntity, Integer> {
}