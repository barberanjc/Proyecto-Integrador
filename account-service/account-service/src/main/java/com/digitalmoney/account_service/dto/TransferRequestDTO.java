package com.digitalmoney.account_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDTO {

    @NotBlank(message = "El tipo de destino (alias/CBU/CVU) es obligatorio")
    private String destinationType;

    @NotBlank(message = "El destino (alias/CBU/CVU) es obligatorio")
    private String destination;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double amount;

    private String description;

    private LocalDateTime date;
}
