package com.digitalmoney.account_service.repository;

import com.digitalmoney.account_service.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByAccountId(Long accountId);
    Optional<Card> findByIdAndAccountId(Long id, Long accountId);
}