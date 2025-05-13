package com.fund.app.box.dto;

import com.fund.app.box.model.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddMoneyRequest {
    @NotBlank(message = "UniqueIdentifier cannot be null ")
    private String uniqueIdentifier;

    @NotNull(message = "Provide not null amount")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;
}
