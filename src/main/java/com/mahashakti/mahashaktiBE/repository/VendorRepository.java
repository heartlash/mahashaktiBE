package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Integer> {
}
