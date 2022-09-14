package com.wasacz.hfms.utils.importer;

import com.wasacz.hfms.finance.ServiceType;
import com.wasacz.hfms.finance.transaction.AbstractTransaction;
import com.wasacz.hfms.finance.transaction.TransactionServiceFactory;
import com.wasacz.hfms.finance.transaction.expense.ExpenseObj;
import com.wasacz.hfms.finance.transaction.income.IncomeObj;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.wasacz.hfms.finance.ServiceType.EXPENSE;
import static com.wasacz.hfms.finance.ServiceType.INCOME;

@Service
public class ImportServiceImpl implements ImportService {

    private final UserRepository userRepository;
    private final TransactionServiceFactory transactionServiceFactory;

    public ImportServiceImpl(UserRepository userRepository, TransactionServiceFactory transactionServiceFactory) {
        this.userRepository = userRepository;
        this.transactionServiceFactory = transactionServiceFactory;
    }

    @Override
    public void importData(Long userId, List<ImportRequest.ImportData> importDataList) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found!"));
        importDataList.forEach(importData -> {
                    LocalDate currentDate = LocalDate.parse(importData.getStartDate());
                    for (Double amount : importData.getValues()) {
                        AbstractTransaction transaction = createTransaction(amount,
                                currentDate,
                                importData.getServiceType(),
                                importData.getCategoryId(),
                                importData.getName());
                        if(transaction != null) {
                            if(amount > 0.0) {
                                transactionServiceFactory.getService(importData.getServiceType()).add(transaction, user, null);
                            }
                            currentDate = currentDate.plusMonths(1);
                        }
                    }
                }
        );
    }

    private AbstractTransaction createTransaction(Double amount, LocalDate date, ServiceType serviceType, Long categoryId, String name) {
        if(EXPENSE.equals(serviceType)) {
            return ExpenseObj.builder().transactionDate(date).amount(amount).categoryId(categoryId).expenseName(name).build();
        }
        if(INCOME.equals(serviceType)) {
            return IncomeObj.builder().transactionDate(date).amount(amount).categoryId(categoryId).name(name).build();
        }
        return null;
    }
}
