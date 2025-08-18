package com.digitalmoney.account_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String type; // "DEPOSIT", "WITHDRAW", etc.

    @Column(nullable = false)
    private LocalDateTime date;

    private String description;
}