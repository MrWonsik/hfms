package com.wasacz.hfms.finance.category.controller;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CategoryObj {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
    private final Double maximumCost;
}
