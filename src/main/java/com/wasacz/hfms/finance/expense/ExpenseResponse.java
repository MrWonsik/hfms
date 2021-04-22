package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseResponse {
    private final Long id;
    private final String expenseName;
    private final String shopName;
    private final Double cost;
    private final DateTime createdDate;
    private final List<ExpensePositionResponse> expensePositionList;
    private final Long receiptId;
}
