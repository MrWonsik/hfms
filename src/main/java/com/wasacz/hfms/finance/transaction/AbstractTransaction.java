package com.wasacz.hfms.finance.transaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wasacz.hfms.finance.transaction.expense.ExpenseObj;
import com.wasacz.hfms.finance.transaction.income.IncomeObj;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "transactionType")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = ExpenseObj.class, name = "EXPENSE"),
                @JsonSubTypes.Type(value = IncomeObj.class, name = "INCOME")
        }
    )
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTransaction {
    private final Long id;
    private final Long categoryId;
    private final String name;
    private final Double amount;
    private final String transactionType;
    @JsonSerialize(using = ToStringSerializer.class)
    private final LocalDate transactionDate;
}
