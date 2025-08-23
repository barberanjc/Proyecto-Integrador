package com.digitalmoney.account_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;
    private String cardHolder;
    private String expirationDate;
    private String cvv;
    private String type;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}