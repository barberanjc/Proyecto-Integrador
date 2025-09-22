package com.digitalmoney.account_service.controller;

import com.digitalmoney.account_service.dto.CardDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.model.Card;
import com.digitalmoney.account_service.security.JwtUtil;
import com.digitalmoney.account_service.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Crear tarjeta", description = "Crea una nueva tarjeta de débito o crédito")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarjeta creada correctamente")
    })
    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody CardDTO dto) {
        Card created = cardService.createCard(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Asociar tarjeta a cuenta", description = "Asocia una tarjeta existente a una cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarjeta asociada correctamente")
    })
    @PostMapping("/accounts/{id}/cards")
    public ResponseEntity<Card> associateCardToAccount(
            @PathVariable Long id,
            @RequestBody CardDTO dto,
            @RequestHeader("Authorization") String token) {

        if (!cardService.isAccountOwnedByToken(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Card associated = cardService.associateCardToAccount(id, dto);
        return ResponseEntity.status(201).body(associated);
    }

    @Operation(summary = "Listar tarjetas de cuenta", description = "Devuelve todas las tarjetas asociadas a una cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjetas encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron tarjetas"),
            @ApiResponse(responseCode = "400", description = "ID de cuenta inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/accounts/{accountId}/cards")
    public ResponseEntity<?> getCardsByAccount(
            @PathVariable Long accountId,
            @RequestHeader("Authorization") String token) {

        try {
            if (!cardService.isAccountOwnedByToken(accountId, token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
            }

            List<Card> cards = cardService.getCardsByAccount(accountId);
            if (cards.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron tarjetas asociadas a la cuenta " + accountId);
            }

            return ResponseEntity.ok(cards);

        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("ID de cuenta inválido");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @Operation(summary = "Detalle de tarjeta", description = "Devuelve la información detallada de una tarjeta específica")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle obtenido"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("account/{accountId}/cards/{cardId}")
    public ResponseEntity<?> getCardDetail(
            @PathVariable Long accountId,
            @PathVariable Long cardId,
            @RequestHeader("Authorization") String token) {

        try {
            if (!cardService.isAccountOwnedByToken(accountId, token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
            }

            CardDTO card = cardService.getCardDetail(accountId, cardId);
            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarjeta no encontrada");
            }

            return ResponseEntity.ok(card);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @Operation(summary = "Eliminar tarjeta", description = "Elimina una tarjeta asociada a una cuenta")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta eliminada"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping("/accounts/{accountId}/cards/{cardId}")
    public ResponseEntity<?> deleteCard(
            @PathVariable Long accountId,
            @PathVariable Long cardId,
            @RequestHeader("Authorization") String token) {

        try {
            if (!cardService.isAccountOwnedByToken(accountId, token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos");
            }

            cardService.deleteCard(accountId, cardId);
            return ResponseEntity.ok("Tarjeta eliminada exitosamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}
