package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.persistence.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.List;

public interface ITransactionService {

    AbstractTransactionResponse add(AbstractTransaction abstractTransaction, User user, MultipartFile file);

    AbstractTransactionResponse getTransaction(long transactionId, User user);

    List<AbstractTransactionResponse> getAll(User user);

    List<AbstractTransactionResponse> getAllForMonthInYear(User user, YearMonth yearMonth);

    AbstractTransactionResponse delete(long transactionId, User user);

    AbstractTransactionResponse addFile(MultipartFile file, User user);

    TransactionType getService();
}
