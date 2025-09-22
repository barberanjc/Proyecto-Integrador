package com.digitalmoney.account_service.service;


import com.digitalmoney.account_service.client.UsuarioClient;
import com.digitalmoney.account_service.dto.TransferHistoryDTO;
import com.digitalmoney.account_service.dto.TransferRequest;
import com.digitalmoney.account_service.dto.TransferRequestDTO;
import com.digitalmoney.account_service.exception.AccountNotFoundException;
import com.digitalmoney.account_service.exception.InsufficientFundsException;
import com.digitalmoney.account_service.exception.TransactionNotFoundException;
import com.digitalmoney.account_service.model.Account;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.repository.AccountRepository;
import com.digitalmoney.account_service.repository.TransactionRepository;
import com.digitalmoney.account_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Transaction> getLastFiveTransactions(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));
        return transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }
    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        return transactionRepository.findByAccountIdOrderByDateDesc(accountId);
    }

    @Override
    public Transaction getTransactionById(Long accountId, Long transactionId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        return transactionRepository.findByIdAndAccountId(transactionId, accountId)
                .orElseThrow(() -> new TransactionNotFoundException("Transacción no encontrada"));
    }
    @Override
    public Transaction addMoneyFromCard(Long accountId, TransferRequest request) {
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        account.setBalance(account.getBalance() + request.getAmount());
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .type("INGRESO")
                .date(LocalDateTime.now())
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public List<TransferHistoryDTO> getLastRecipients(Long accountId, int limit) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByDateDesc(accountId);

        return transactions.stream()
                .filter(tx -> "EGRESO".equals(tx.getType()) && tx.getDestination() != null)
                .limit(limit <= 0 ? 5 : limit)
                .map(tx -> new TransferHistoryDTO(
                        tx.getDestinationType(),
                        tx.getDestination(),
                        tx.getAmount()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Transaction transferMoney(Long accountId, TransferRequestDTO request) {
        Account source = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));

        if (source.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Fondos insuficientes para realizar la transferencia");
        }

        Optional<Account> destinationAccountOpt = accountRepository.findByAlias(request.getDestination());

        if (destinationAccountOpt.isEmpty()) {
            destinationAccountOpt = accountRepository.findByCvu(request.getDestination());
        }

        Account destinationAccount = destinationAccountOpt
                .orElseThrow(() -> new AccountNotFoundException("Cuenta destino no encontrada"));

        if (source.getId().equals(destinationAccount.getId())) {
            throw new IllegalArgumentException("No puede transferirse dinero a sí mismo");
        }

        source.setBalance(source.getBalance() - request.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + request.getAmount());

        accountRepository.save(source);
        accountRepository.save(destinationAccount);

        Transaction tx = Transaction.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .type("EGRESO")
                .date(LocalDateTime.now())
                .description(request.getDescription())
                .destination(request.getDestination())
                .destinationType(request.getDestinationType())
                .build();

        return transactionRepository.save(tx);
    }
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioClient usuarioClient;

    public boolean isAccountOwnedByToken(Long accountId, String token) {
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));
        Long userIdFromToken = usuarioClient.getUsuarioByEmail(email).getId();
        return account.getUserId().equals(userIdFromToken);
    }
}