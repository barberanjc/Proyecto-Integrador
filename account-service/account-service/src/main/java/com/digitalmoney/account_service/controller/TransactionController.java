package com.digitalmoney.account_service.controller;

import com.digitalmoney.account_service.dto.TransferRequest;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.exception.TransactionNotFoundException;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{accountId}/activity/{transactionId}")
    public ResponseEntity<?> getTransactionDetail(
            @PathVariable Long accountId,
            @PathVariable Long transactionId,
            @RequestHeader("Authorization") String token) {

        try {
            Transaction transaction = transactionService.getTransactionById(accountId, transactionId);
            return ResponseEntity.ok(transaction);
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cuenta no encontrada");
        } catch (TransactionNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transacción no encontrada");
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permisos");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @PostMapping("/{id}/transferences")
    public ResponseEntity<?> addMoneyFromCard(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody TransferRequest request) {
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

}
