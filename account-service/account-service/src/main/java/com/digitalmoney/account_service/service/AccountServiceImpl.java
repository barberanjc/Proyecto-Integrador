package com.digitalmoney.account_service.service;

import com.digitalmoney.account_service.dto.AccountDTO;
import com.digitalmoney.account_service.model.Account;
import com.digitalmoney.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.digitalmoney.account_service.exception.AccountNotFoundException;



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
    public void createAccount(Long userId, AccountDTO dto) {
        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(0.0);
        account.setAlias(dto.getAlias());
        account.setCvu(dto.getCvu());
        accountRepository.save(account);
    }

    @Override
    public AccountDTO getAccountProfile(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada para el usuario: " + userId));

        return new AccountDTO(account.getCvu(), account.getAlias());
    }
}