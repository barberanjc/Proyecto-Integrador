package com.digitalmoney.account_service.service;

import com.digitalmoney.account_service.dto.CardDTO;
import com.digitalmoney.account_service.model.Card;

import java.util.List;

public interface CardService {
    Card createCard(CardDTO dto);
    Card associateCardToAccount(Long accountId, CardDTO dto);
    List<Card> getCardsByAccount(Long accountId);
    CardDTO getCardDetail(Long accountId, Long cardId);
    void deleteCard(Long accountId, Long cardId);
}