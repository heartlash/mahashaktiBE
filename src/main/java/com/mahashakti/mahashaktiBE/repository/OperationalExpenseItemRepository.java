package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.OperationalExpenseItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationalExpenseItemRepository extends JpaRepository<OperationalExpenseItemEntity, Integer> {
}
