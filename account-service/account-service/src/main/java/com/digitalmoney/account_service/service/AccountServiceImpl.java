package com.digitalmoney.account_service.service;

import com.digitalmoney.account_service.model.Account;
import com.digitalmoney.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.digitalmoney.account_service.exception.AccountNotFoundException;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Double getBalanceByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"))
                .getBalance();
    }
    @Override
    public void createAccount(Long userId) {
        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(0.0);
        account.setAlias(generateRandomAlias());

        accountRepository.save(account);
    }

    private String generateRandomAlias() {
        return UUID.randomUUID().toString().substring(0, 20).replace("-", ".");
    }

}