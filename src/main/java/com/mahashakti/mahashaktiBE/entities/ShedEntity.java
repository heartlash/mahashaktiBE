package com.mahashakti.mahashaktiBE.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "sheds")
public class ShedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "baby", nullable = false)
    private Boolean baby;

    @Column(name = "genesis_date", nullable = false)
    private Date genesisDate;

}
