package com.digitalmoney.account_service.dto;

import com.digitalmoney.account_service.model.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    private String cardNumber;
    private String cardHolder;
    private String expirationDate;
    private String cvv;
    private String type;

    public CardDTO(Card card) {
        this.cardNumber = card.getCardNumber();
        this.cardHolder = card.getCardHolder();
        this.expirationDate = card.getExpirationDate();
        this.cvv = card.getCvv();
        this.type = card.getType();
    }
}