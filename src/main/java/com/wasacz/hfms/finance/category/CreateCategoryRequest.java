package com.wasacz.hfms.finance.category;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CreateCategoryRequest {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
    private final CategoryType categoryType;
    private final Double maximumCost;
}
