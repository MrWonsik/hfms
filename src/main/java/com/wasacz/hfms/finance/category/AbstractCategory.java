package com.wasacz.hfms.finance.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCategory {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
}
