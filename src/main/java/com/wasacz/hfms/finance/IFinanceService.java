package com.wasacz.hfms.finance;

import com.wasacz.hfms.persistence.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFinanceService {

    AbstractFinanceResponse add(AbstractFinance abstractFinance, User user, MultipartFile file);

    List<AbstractFinanceResponse> getAll(User user);

    FinanceType getService();
}
