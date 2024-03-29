package com.wasacz.hfms.finance.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "categoryType")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = IncomeCategoryObj.class, name = "INCOME"),
                @JsonSubTypes.Type(value = ExpenseCategoryObj.class, name = "EXPENSE")
        }
)
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCategory {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
    private final String categoryType;
}
