package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.persistence.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITransactionService {

    AbstractTransactionResponse add(AbstractTransaction abstractTransaction, User user, MultipartFile file);

    List<AbstractTransactionResponse> getAll(User user);

    TransactionType getService();
}
