package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.finance.ServiceType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class TransactionServiceFactory {

    private Map<ServiceType, ITransactionService> financeServices;

    public TransactionServiceFactory(Set<ITransactionService> categoryServiceSet) {
        createCategoryService(categoryServiceSet);
    }

    public ITransactionService getService(ServiceType transactionType) {
        return financeServices.get(transactionType);
    }
    private void createCategoryService(Set<ITransactionService> categoryServiceSet) {
        financeServices = new HashMap<>();
        categoryServiceSet.forEach(
                financeService -> financeServices.put(financeService.getService(), financeService));
    }
}
