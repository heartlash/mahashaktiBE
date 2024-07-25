package com.mahashakti.mahashaktiBE.entities;


import com.mahashakti.mahashaktiBe.model.Unit;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "materials")
public class MaterialEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "unit_id", referencedColumnName = "id", nullable = false)
    private UnitEntity unit;

}
