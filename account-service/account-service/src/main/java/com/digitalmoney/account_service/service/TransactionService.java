package com.digitalmoney.account_service.service;

import com.digitalmoney.account_service.model.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionsByAccountId(Long accountId);
}