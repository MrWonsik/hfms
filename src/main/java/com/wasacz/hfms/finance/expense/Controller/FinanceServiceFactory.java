package com.wasacz.hfms.finance.expense.Controller;

import com.wasacz.hfms.finance.FinanceType;
import com.wasacz.hfms.finance.IFinanceService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class FinanceServiceFactory {

    private Map<FinanceType, IFinanceService> financeServices;

    public FinanceServiceFactory(Set<IFinanceService> categoryServiceSet) {
        createCategoryService(categoryServiceSet);
    }

    public IFinanceService getService(FinanceType financeType) {
        return financeServices.get(financeType);
    }
    private void createCategoryService(Set<IFinanceService> categoryServiceSet) {
        financeServices = new HashMap<>();
        categoryServiceSet.forEach(
                financeService -> financeServices.put(financeService.getService(), financeService));
    }
}
