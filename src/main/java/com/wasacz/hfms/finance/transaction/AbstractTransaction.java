package com.wasacz.hfms.finance.transaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wasacz.hfms.finance.transaction.expense.ExpenseObj;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "transactionType")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = ExpenseObj.class, name = "EXPENSE"),
        }
    )
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTransaction {
    private final Long id;
    private final Long categoryId;
    private final String name;
    private final Double cost;
    private final String transactionType;
    private final LocalDate transactionDate;
}
