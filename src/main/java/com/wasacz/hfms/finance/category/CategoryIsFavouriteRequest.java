package com.wasacz.hfms.finance.category;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryIsFavouriteRequest {

    private final CategoryType categoryType;
    private final Boolean isFavourite;
}
