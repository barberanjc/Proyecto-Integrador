package com.digitalmoney.account_service.controller;

import com.digitalmoney.account_service.dto.CardDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.model.Card;
import com.digitalmoney.account_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody CardDTO dto) {
        Card created = cardService.createCard(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/accounts/{id}/cards")
    public ResponseEntity<Card> associateCardToAccount(@PathVariable Long id, @RequestBody CardDTO dto) {
        Card associated = cardService.associateCardToAccount(id, dto);
        return ResponseEntity.status(201).body(associated);
    }

    @GetMapping("/accounts/{accountId}/cards")
    public ResponseEntity<?> getCardsByAccount(@PathVariable Long accountId) {
        try {
            List<Card> cards = cardService.getCardsByAccount(accountId);

            if (cards.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron tarjetas asociadas a la cuenta " + accountId);
            }

            return ResponseEntity.ok(cards); // 200 OK
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("ID de cuenta inválido"); // 400
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos"); // 403
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno"); // 500
        }
    }

    @GetMapping("account/{accountId}/cards/{cardId}")
    public ResponseEntity<?> getCardDetail(
            @PathVariable Long accountId,
            @PathVariable Long cardId) {
        try {
            CardDTO card = cardService.getCardDetail(accountId, cardId);

            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tarjeta no encontrada");
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
    @DeleteMapping("/accounts/{accountId}/cards/{cardId}")
    public ResponseEntity<?> deleteCard(
            @PathVariable Long accountId,
            @PathVariable Long cardId) {
        try {
            cardService.deleteCard(accountId, cardId);
            return ResponseEntity.ok("Tajeta eliminada exitosamente");
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
