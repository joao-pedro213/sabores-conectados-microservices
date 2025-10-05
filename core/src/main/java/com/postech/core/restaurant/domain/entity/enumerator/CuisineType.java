package com.postech.core.restaurant.domain.entity.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CuisineType {
    ITALIAN("Italian"),
    MEXICAN("Mexican"),
    CHINESE("Chinese"),
    INDIAN("Indian"),
    JAPANESE("Japanese"),
    THAI("Thai"),
    FRENCH("French"),
    BRAZILIAN("Brazilian"),
    AMERICAN("American"),
    GREEK("Greek"),
    VIETNAMESE("Vietnamese"),
    SPANISH("Spanish");

    private final String value;
}
