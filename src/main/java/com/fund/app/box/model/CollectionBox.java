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

    @OneToMany(mappedBy = "collectionBox", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
