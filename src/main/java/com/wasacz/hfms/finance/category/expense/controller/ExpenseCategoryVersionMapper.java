package com.wasacz.hfms.finance.category.expense.controller;

import com.wasacz.hfms.persistence.ExpenseCategoryVersion;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpenseCategoryVersionMapper {

    public ExpenseCategoryVersionResponse mapExpenseCategoryVersionToResponse(ExpenseCategoryVersion expenseCategoryVersion) {
        return ExpenseCategoryVersionResponse.builder()
                .id(expenseCategoryVersion.getId())
                .maximumAmount(expenseCategoryVersion.getMaximumAmount().doubleValue())
                .isValid(expenseCategoryVersion.getMaximumAmount().doubleValue() != 0)
                .createDate(new DateTime(expenseCategoryVersion.getCreatedDate()))
                .validMonth(expenseCategoryVersion.getValidMonth())
                .build();
    }

    public List<ExpenseCategoryVersionResponse> mapExpenseCategoryVersionsListToResponse(List<ExpenseCategoryVersion> expenseCategoryVersionList) {
        return expenseCategoryVersionList.stream().map(this::mapExpenseCategoryVersionToResponse).collect(Collectors.toList());
    }
}
