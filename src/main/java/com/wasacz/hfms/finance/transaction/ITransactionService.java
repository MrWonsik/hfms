package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.finance.ServiceType;
import com.wasacz.hfms.persistence.User;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface ITransactionService {

    AbstractTransactionResponse add(AbstractTransaction abstractTransaction, User user, MultipartFile file);

    List<AbstractTransactionResponse> getAll(User user);

    List<AbstractTransactionResponse> getAllForMonthInYear(User user, YearMonth yearMonth);

    AbstractTransactionResponse delete(long transactionId, User user);

    ServiceType getService();

    AbstractTransactionResponse updateTransaction(Long transactionId, AbstractTransaction transaction, User user);

    BigDecimal getSummaryAmountOfCategoryForMonth(long categoryId, YearMonth yearMonth);

    AbstractTransactionResponse getTheOldestTransactionForCategory(long categoryId);

}
