package com.fund.app.box.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignBoxRequest {
    @NotBlank(message = "Collection box id cannot be null")
    private String uniqueIdentifier;
    @NotBlank(message = "Event name cannot be null")
    private String eventName;
}
