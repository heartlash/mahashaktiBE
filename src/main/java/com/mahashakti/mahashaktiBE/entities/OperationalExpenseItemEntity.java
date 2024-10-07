package com.mahashakti.mahashaktiBE.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "operational_expense_items")
public class OperationalExpenseItemEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item", nullable = false, length = 100)
    private String item;

}
