package com.fund.app.box.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Rate {
    private String currency;
    private String code;
    private BigDecimal mid;
}
