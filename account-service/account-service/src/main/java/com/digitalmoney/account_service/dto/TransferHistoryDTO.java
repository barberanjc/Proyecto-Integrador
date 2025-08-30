package com.digitalmoney.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferHistoryDTO {
    private String alias;
    private String cbu;
    private Double montoUltimo;
}