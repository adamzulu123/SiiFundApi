package com.fund.app.box.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionBoxDto {
    //private UUID id;
    private String uniqueIdentifier;
    private boolean isAssigned;
    private boolean isEmpty;
    private String fundraisingEventName;


}
