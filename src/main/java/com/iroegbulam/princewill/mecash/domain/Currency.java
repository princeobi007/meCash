package com.iroegbulam.princewill.mecash.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/** Naira Should not be included in this table as it's the base currency in this application.
 * Buy and sell prices are pegged against the naira
 * My recommendation is to have another service exclusively managed by admin who are responsible for updating rates,
 * based on current market rates.
 * This service is beyond the scope of this service.
 * Only read operations should be performed on this service.
 * For the purpose of this assessment currency will be limited to USD, GBP, EURO, YEN, YUAN
 */
@Table()
@Getter
@Setter
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

    @Override
    public String toString(){
        return code;
    }
}
