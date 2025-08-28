package com.digitalmoney.account_service.service;


import com.digitalmoney.account_service.dto.TransferRequest;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.exception.TransactionNotFoundException;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.repository.AccountRepository;
import com.digitalmoney.account_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Transaction> getLastFiveTransactions(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));
        return transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }
    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        return transactionRepository.findByAccountIdOrderByDateDesc(accountId);
    }

    @Override
    public Transaction getTransactionById(Long accountId, Long transactionId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        return transactionRepository.findByIdAndAccountId(transactionId, accountId)
                .orElseThrow(() -> new TransactionNotFoundException("Transacción no encontrada"));
    }
    @Override
    public Transaction addMoneyFromCard(Long accountId, TransferRequest request) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .type("INGRESO")
                .date(LocalDateTime.now())
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }
}