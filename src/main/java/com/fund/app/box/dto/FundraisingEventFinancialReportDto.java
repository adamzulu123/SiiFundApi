package com.fund.app.box.dto;

import com.fund.app.box.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data //its @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode together
public class FundraisingEventFinancialReportDto {
    private String FundraisingEventName;
    private BigDecimal Amount;
    private Currency Currency;
}
