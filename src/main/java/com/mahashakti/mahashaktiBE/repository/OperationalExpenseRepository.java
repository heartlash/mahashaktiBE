package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.OperationalExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationalExpenseRepository extends JpaRepository<OperationalExpenseEntity, UUID> {
    List<OperationalExpenseEntity> findByExpenseDateBetween(Date startDate, Date endDate);
    List<OperationalExpenseEntity> findByExpenseDateBetweenAndCreatedBy(Date startDate, Date endDate, String createdBy);
    Optional<OperationalExpenseEntity> findTopByItemIdOrderByExpenseDateDesc(Integer operationalExpenseItemId);
    List<OperationalExpenseEntity>  findByExpenseDateBetweenAndItemId(Date startDate, Date endDate, Integer operationalExpenseItemId);
}
