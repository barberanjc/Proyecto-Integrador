package com.digitalmoney.account_service.service;


import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.repository.AccountRepository;
import com.digitalmoney.account_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        return transactionRepository.findByAccountIdOrderByDateDesc(accountId);
    }
}