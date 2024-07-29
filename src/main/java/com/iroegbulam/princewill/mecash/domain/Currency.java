package com.iroegbulam.princewill.mecash.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Table()
@Entity(name = "currency")
@EqualsAndHashCode(of = "id")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String code;

    private double buyPrice;

    private double sellPrice;
}
