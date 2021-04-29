package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.persistence.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.Month;
import java.util.List;

public interface ITransactionService {

    AbstractTransactionResponse add(AbstractTransaction abstractTransaction, User user, MultipartFile file);

    AbstractTransactionResponse getTransaction(long transactionId, User user);

    List<AbstractTransactionResponse> getAll(User user);

    List<AbstractTransactionResponse> getAllForMonth(User user, Month month);

    AbstractTransactionResponse delete(long transactionId, User user);

    AbstractTransactionResponse addFile(MultipartFile file, User user);

    TransactionType getService();
}
