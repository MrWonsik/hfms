package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

import java.time.YearMonth;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseCategoryVersionResponse {
    private final long id;
    private final Double maximumCost;
    private final YearMonth validMonth;
    private final boolean isValid;
    private final DateTime createDate;
}
