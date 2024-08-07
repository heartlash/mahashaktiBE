package com.mahashakti.mahashaktiBE.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Date;

@Data
@Entity
@Table(name = "production")
public class ProductionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "produced_count", nullable = false)
    private Integer producedCount;

    @Column(name = "production_percentage", nullable = false)
    private BigDecimal productionPercentage;

    @Column(name = "broken_count", nullable = false)
    private Integer brokenCount;

    @Column(name = "broken_reason", length = 100, nullable = false)
    private String brokenReason;

    @Column(name = "self_use_count", nullable = false)
    private Integer selfUseCount;

    @Column(name = "gift_count", nullable = false)
    private Integer giftCount;

    @Column(name = "saleable_count", nullable = false)
    private Integer saleableCount;

    @Column(name = "production_date", nullable = false)
    private Date productionDate;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
