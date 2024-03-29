package com.nuvola.gxpenses.server.repos;

import com.nuvola.gxpenses.common.shared.business.Account;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepos extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId, Sort sort);

    @Query("select a.id from Account a where a.user.id = ?1")
    List<Long> findAccountIdByUserId(Long userId);
}
