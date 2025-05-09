package com.fund.app.box.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uniqueIdentifier;
    private boolean isAssigned;

    @ManyToOne(fetch = FetchType.LAZY)
    private FundraisingEvent fundraisingEvent;

    //todo: orphanRemoval = true --> this option is under consideration because maybe we need every money transfer in history
    @OneToMany(mappedBy = "collectionBox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoneyEntry> moneyEntries;

    //helper method to find if box is empty
    public boolean isEmpty(){
        return  moneyEntries == null || moneyEntries.isEmpty();
    }

}
