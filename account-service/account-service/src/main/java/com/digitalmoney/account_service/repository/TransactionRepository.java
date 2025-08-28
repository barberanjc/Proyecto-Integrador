package com.digitalmoney.account_service.repository;

import com.digitalmoney.account_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop5ByAccountIdOrderByDateDesc(Long accountId);
    List<Transaction> findByAccountIdOrderByDateDesc(Long accountId);
    Optional<Transaction> findByIdAndAccountId(Long transactionId, Long accountId);
}