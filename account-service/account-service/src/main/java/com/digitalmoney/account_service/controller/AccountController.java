package com.digitalmoney.account_service.controller;

import com.digitalmoney.account_service.dto.AccountDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.service.AccountService;
import com.digitalmoney.account_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<Double> getAccountBalance(@PathVariable Long id) {
        try {
            Double balance = accountService.getBalanceByUserId(id);
            return ResponseEntity.ok(balance);
        } catch (AccountNotFoundException e) {
            System.out.println("No se encontró una cuenta para el usuario ID: " + id);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<String> createAccount(
            @PathVariable Long userId,
            @RequestBody AccountDTO accountDTO) {
        try {
            accountService.createAccount(userId, accountDTO);
            return ResponseEntity.ok("Cuenta creada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la cuenta: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long id) {
        try {
            List<Transaction> transactions = transactionService.getLastFiveTransactions(id);
            return ResponseEntity.ok(transactions);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{userId}/profile")
    public ResponseEntity<AccountDTO> getAccountProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountProfile(userId));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getAccountActivity(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            List<Transaction> activities = transactionService.getTransactionsByAccountId(id);
            return ResponseEntity.ok(activities);
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cuenta no encontrada");
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permisos");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno");
        }
    }
}