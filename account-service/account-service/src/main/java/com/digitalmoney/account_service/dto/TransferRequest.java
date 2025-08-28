package com.digitalmoney.account_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private Long cardId;
    private Double amount;
    private String description;
}
