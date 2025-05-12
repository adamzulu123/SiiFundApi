package com.fund.app.box.dto;

import com.fund.app.box.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateFundraisingEventRequest {
    @NotBlank(message = "Event name cannot be blank")
    private String eventName;
    @NotNull(message = "Account currency has to be specified")
    private Currency accountCurrency;
}
