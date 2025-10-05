package com.postech.identityservice.data.document.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemRole {
    CUSTOMER("CUSTOMER"),
    RESTAURANT_OWNER("RESTAURANT_OWNER"),
    RESTAURANT_MANAGER("RESTAURANT_MANAGER");

    private final String value;
}
