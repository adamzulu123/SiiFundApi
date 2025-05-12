package com.fund.app.box.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCollectionBoxRequest {
    private String eventName; // Optional event name to associate with
}
