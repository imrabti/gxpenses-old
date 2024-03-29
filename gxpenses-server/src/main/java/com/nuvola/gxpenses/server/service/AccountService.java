package com.nuvola.gxpenses.server.service;

import com.nuvola.gxpenses.common.shared.business.Account;

import java.util.List;

public interface AccountService {
    void createAccount(Account account);

    void removeAccount(Long accountId);

    List<Account> findAllAccountsByUserId();
}
