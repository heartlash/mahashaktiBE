package com.mahashakti.mahashaktiBE.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "sale")
public class SaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "sold_count", nullable = false)
    private Integer soldCount;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private VendorEntity vendor;

    @Column(name = "sale_date", nullable = false)
    private Date saleDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}