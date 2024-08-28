package com.mahashakti.mahashaktiBE.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "material_stock")
@Data
public class MaterialStockEntity {

    @Id
    @Column(name = "material_id")
    private Integer materialId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "min_quantity", nullable = false)
    private BigDecimal minQuantity;

    @Column(name = "last_purchase_date", nullable = false)
    private Date lastPurchaseDate;

    @Column(name = "last_purchase_rate", nullable = false)
    private BigDecimal lastPurchaseRate;
}
