package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.finance.shop.ShopObj;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class ExpenseObj {
    private final Long id;
    private final Long categoryId;
    private final String expenseName;
    private final Double cost;
    private final ShopObj shop;
//    private final MultipartFile multipartFile;
    private final List<ExpensePositionObj> expensePositions;
}
