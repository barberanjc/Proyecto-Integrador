package com.digitalmoney.account_service.controller;

import com.digitalmoney.account_service.dto.TransferHistoryDTO;
import com.digitalmoney.account_service.dto.TransferRequest;
import com.digitalmoney.account_service.dto.TransferRequestDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.exception.InsufficientFundsException;
import com.digitalmoney.account_service.exception.TransactionNotFoundException;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.security.JwtUtil;
import com.digitalmoney.account_service.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Detalle de transacción", description = "Devuelve la información detallada de una transacción específica de una cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{accountId}/activity/{transactionId}")
    public ResponseEntity<?> getTransactionDetail(
            @PathVariable Long accountId,
            @PathVariable Long transactionId,
            @RequestHeader("Authorization") String token) {

        if (!transactionService.isAccountOwnedByToken(accountId, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        }

        try {
            Transaction transaction = transactionService.getTransactionById(accountId, transactionId);
            return ResponseEntity.ok(transaction);
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta no encontrada");
        } catch (TransactionNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transacción no encontrada");
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @Operation(summary = "Recargar cuenta desde tarjeta", description = "Agrega dinero a la cuenta desde una tarjeta existente")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recarga exitosa"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping("/{id}/transferences")
    public ResponseEntity<?> addMoneyFromCard(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody TransferRequest request) {

        if (!transactionService.isAccountOwnedByToken(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        }

        try {
            Transaction transaction = transactionService.addMoneyFromCard(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @Operation(summary = "Transferir dinero entre cuentas", description = "Realiza una transferencia desde la cuenta origen a otra cuenta usando alias o CVU")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferencia exitosa"),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "410", description = "Fondos insuficientes"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping("/{id}/transfers")
    public ResponseEntity<?> transferMoney(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TransferRequestDTO request) {

        if (!transactionService.isAccountOwnedByToken(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        }

        try {
            Transaction transaction = transactionService.transferMoney(id, request);
            return ResponseEntity.ok(transaction);
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta no encontrada");
        } catch (InsufficientFundsException ex) {
            return ResponseEntity.status(HttpStatus.GONE).body("Fondos insuficientes");
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @Operation(summary = "Últimos destinatarios", description = "Devuelve los últimos destinatarios de transferencias realizadas desde la cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Destinatarios encontrados"),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "No se encontraron destinatarios"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/{id}/transferences")
    public ResponseEntity<?> getLastRecipients(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        if (!transactionService.isAccountOwnedByToken(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        }

        try {
            List<TransferHistoryDTO> recipients = transactionService.getLastRecipients(id, 10);

            if (recipients.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron destinatarios para esta cuenta");
            }

            return ResponseEntity.ok(recipients);

        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta no encontrada");
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
}