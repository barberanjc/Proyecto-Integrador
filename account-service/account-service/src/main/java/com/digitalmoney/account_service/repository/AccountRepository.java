package com.digitalmoney.account_service.repository;

import com.digitalmoney.account_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);
    Optional<Account> findByAlias(String alias);
    Optional<Account> findByCvu(String cvu);
}