package com.fund.app.box.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExchangeRateDto {
    private String table; //A - avg rates for most common currencies
    private String no;
    private String effectiveDate;
    private List<Rate> rates;
}
