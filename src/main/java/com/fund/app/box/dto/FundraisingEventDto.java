package com.fund.app.box.dto;

import com.fund.app.box.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FundraisingEventDto {
    private Long id;
    private String eventName;
    private Currency accountCurrency;
    private BigDecimal accountBalance;
}

