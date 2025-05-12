package com.fund.app.box.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class MoneyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    private CollectionBox collectionBox;

}
