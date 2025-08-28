package com.digitalmoney.account_service.service;

import com.digitalmoney.account_service.dto.TransferRequest;
import com.digitalmoney.account_service.model.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> getLastFiveTransactions(Long accountId);
    List<Transaction> getTransactionsByAccountId(Long accountId);
    Transaction getTransactionById(Long accountId, Long transactionId);
    Transaction addMoneyFromCard(Long accountId, TransferRequest request);
}