package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.ServiceType;
import com.wasacz.hfms.finance.transaction.AbstractTransactionResponse;
import com.wasacz.hfms.finance.transaction.TransactionServiceFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
class TransactionSummaryProvider {

    private final TransactionServiceFactory transactionServiceFactory;

    TransactionSummaryProvider(TransactionServiceFactory transactionServiceFactory) {
        this.transactionServiceFactory = transactionServiceFactory;
    }

    Map<YearMonth, Double> getTransactionMapProvider(long categoryId, ServiceType serviceType) {
        AbstractTransactionResponse theOldestTransactionForCategory = transactionServiceFactory.getService(serviceType).getTheOldestTransactionForCategory(categoryId);
        if(theOldestTransactionForCategory == null) {
            return Collections.emptyMap();
        }

        LocalDate now = LocalDate.now();
        LocalDate createdDate = theOldestTransactionForCategory.getCreatedDate().withDayOfMonth(1);
        Map<YearMonth, Double> summaryMap = new HashMap<>();

        while(!createdDate.isAfter(now)) {
            YearMonth yearMonth = LocalDateToYearMonthConverter.convertToYearMonth(createdDate);
            summaryMap.put(yearMonth, transactionServiceFactory.getService(serviceType).getSummaryAmountOfCategoryForMonth(categoryId, yearMonth).doubleValue());
            createdDate = createdDate.plusMonths(1);
        }

        return summaryMap;
    }
}
