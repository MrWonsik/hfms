package com.wasacz.hfms.finance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wasacz.hfms.finance.expense.ExpenseObj;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = ExpenseObj.class, name = "ExpenseObj"),
        }
    )
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractFinance {
    private final Long id;
    private final Long categoryId;
    private final String expenseName;
    private final Double cost;
}
