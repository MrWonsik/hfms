package com.wasacz.hfms.finance.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.income.IncomeCategoryObj;
import lombok.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = IncomeCategoryObj.class, name = "IncomeCategoryObj"),
                @JsonSubTypes.Type(value = ExpenseCategoryObj.class, name = "ExpenseCategoryObj")
        }
)
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCategory {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
}
