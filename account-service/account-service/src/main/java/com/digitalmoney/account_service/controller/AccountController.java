package com.digitalmoney.account_service.controller;

import com.digitalmoney.account_service.dto.AccountDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.security.JwtUtil;
import com.digitalmoney.account_service.service.AccountService;
import com.digitalmoney.account_service.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;


@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Consultar saldo de cuenta", description = "Devuelve el saldo disponible de una cuenta por ID de usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Double> getAccountBalance(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Long userIdFromToken = accountService.getUserIdByEmail(email);

            if (!userIdFromToken.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Double balance = accountService.getBalanceByUserId(id);
            return ResponseEntity.ok(balance);

        } catch (AccountNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Crear cuenta", description = "Crea una nueva cuenta asociada a un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta creada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error al crear la cuenta")
    })
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

    @Operation(summary = "Últimas transacciones", description = "Devuelve las últimas 5 transacciones de una cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacciones obtenidas"),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Long userId = accountService.getUserIdByEmail(email);

            if (!userId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Transaction> transactions = transactionService.getLastFiveTransactions(id);
            return ResponseEntity.ok(transactions);

        } catch (AccountNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Perfil de cuenta", description = "Devuelve el CVU y alias de la cuenta asociada al usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{userId}/profile")
    public ResponseEntity<AccountDTO> getAccountProfile(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Long userIdFromToken = accountService.getUserIdByEmail(email);

        if (!userIdFromToken.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(accountService.getAccountProfile(userId));
    }

    @Operation(summary = "Actividad de cuenta", description = "Devuelve el historial completo de transacciones de una cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actividad obtenida"),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getAccountActivity(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Long userId = accountService.getUserIdByEmail(email);

            if (!userId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
            }

            List<Transaction> activities = transactionService.getTransactionsByAccountId(id);
            return ResponseEntity.ok(activities);

        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta no encontrada");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
}