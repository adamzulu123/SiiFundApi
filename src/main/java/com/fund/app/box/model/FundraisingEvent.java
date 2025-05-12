package com.fund.app.box.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class FundraisingEvent {

    public FundraisingEvent(String eventName, Currency accountCurrency) {
        this.eventName = eventName;
        this.accountCurrency = accountCurrency;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency accountCurrency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "fundraisingEvent", fetch = FetchType.LAZY)
    private List<CollectionBox> collectionBoxes = new ArrayList<>();
}
