package com.digitalmoney.account_service.service;

import com.digitalmoney.account_service.client.UsuarioClient;
import com.digitalmoney.account_service.dto.CardDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.model.Account;
import com.digitalmoney.account_service.model.Card;
import com.digitalmoney.account_service.repository.AccountRepository;
import com.digitalmoney.account_service.repository.CardRepository;
import com.digitalmoney.account_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Card createCard(CardDTO dto) {
        cardRepository.findByCardNumber(dto.getCardNumber()).ifPresent(c -> {
            throw new RuntimeException("409: Tarjeta ya existe asociada a otra cuenta");
        });
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardHolder(dto.getCardHolder());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCvv(dto.getCvv());
        card.setType(dto.getType());

        return cardRepository.save(card);
    }

    @Override
    public Card associateCardToAccount(Long accountId, CardDTO dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        Card card = cardRepository.findByCardNumber(dto.getCardNumber())
                .orElseGet(() -> createCard(dto));

        if (card.getAccount() != null && !card.getAccount().getId().equals(accountId)) {
            throw new RuntimeException("409: La tarjeta ya está asociada a otra cuenta");
        }

        card.setAccount(account);
        return cardRepository.save(card);
    }

    @Override
    public List<Card> getCardsByAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Cuenta no encontrada");
        }
        return cardRepository.findByAccountId(accountId);
    }
    @Override
    public CardDTO getCardDetail(Long accountId, Long cardId) {
        Optional<Card> cardOpt = cardRepository.findByIdAndAccountId(cardId, accountId);

        if (cardOpt.isEmpty()) {
            return null;
        }
        Card card = cardOpt.get();
        return new CardDTO(card);
    }
    @Override
    public void deleteCard(Long accountId, Long cardId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Cuenta no encontrada"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchElementException("Tarjeta no encontrada"));

        if (!card.getAccount().getId().equals(account.getId())) {
            throw new SecurityException("Esta tarjeta no pertenece a la cuenta dada");
        }

        cardRepository.delete(card);
    }

    @Override
    public boolean isAccountOwnedByToken(Long accountId, String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        Long userIdFromToken = getUserIdByEmail(email);

        return account.getUserId().equals(userIdFromToken);
    }
    @Autowired
    private UsuarioClient usuarioClient;

    private Long getUserIdByEmail(String email) {
        return usuarioClient.getUsuarioByEmail(email).getId();
    }

}