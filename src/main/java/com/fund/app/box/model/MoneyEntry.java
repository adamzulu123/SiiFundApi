package com.fund.app.box.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class MoneyEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    private CollectionBox collectionBox;

    //to track transactions time
    private LocalDateTime createTime;

//    @PrePersist
//    protected void onCreate() {
//        this.createTime = LocalDateTime.now();
//    }
}
