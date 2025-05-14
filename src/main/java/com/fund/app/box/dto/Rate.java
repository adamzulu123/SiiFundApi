package com.fund.app.box.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Rate {
    private String currency;
    private String code;
    private BigDecimal mid;
}
