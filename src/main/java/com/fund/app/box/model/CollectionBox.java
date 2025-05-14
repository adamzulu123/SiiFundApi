package com.fund.app.box.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CollectionBox {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uniqueIdentifier;

    @ManyToOne(fetch = FetchType.LAZY)
    private FundraisingEvent fundraisingEvent;

    //there is important question if we should delete all transaction history or not,
    //right now we are deleting collection box with all its MoneyEntry information.
    //It might be easier to implement and sometimes more suitable, but it's also highly possible
    //that we need all transactions and boxes history available everytime for analysis
    //todo:: this case might be important to discuss an analyze and implement right solution
    @OneToMany(mappedBy = "collectionBox", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoneyEntry> moneyEntries = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.uniqueIdentifier = UUID.randomUUID().toString();
    }

    @Transient
    public boolean isAssigned() {
        return this.fundraisingEvent != null;
    }

    @Transient
    public boolean isEmpty() {
        return this.moneyEntries.isEmpty();
    }

}
