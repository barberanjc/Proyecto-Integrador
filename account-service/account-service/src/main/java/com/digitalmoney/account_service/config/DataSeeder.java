package com.digitalmoney.account_service.config;

import com.digitalmoney.account_service.model.Account;
import com.digitalmoney.account_service.model.Card;
import com.digitalmoney.account_service.model.Transaction;
import com.digitalmoney.account_service.repository.AccountRepository;
import com.digitalmoney.account_service.repository.CardRepository;
import com.digitalmoney.account_service.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public DataSeeder(AccountRepository accountRepository,
                      CardRepository cardRepository,
                      TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (accountRepository.count() > 0) return;

        Account cuentaJuan = new Account();
        cuentaJuan.setId(1L);
        cuentaJuan.setUserId(1L);
        cuentaJuan.setAlias("brote.paraje.cielo");
        cuentaJuan.setCvu("0003979508548596459163");
        cuentaJuan.setBalance(1000.0);
        accountRepository.save(cuentaJuan);

        Account cuentaCarlos = new Account();
        cuentaCarlos.setId(2L);
        cuentaCarlos.setUserId(2L);
        cuentaCarlos.setAlias("pozo.volcán.cascada");
        cuentaCarlos.setCvu("0000763866839600730337");
        cuentaCarlos.setBalance(900.0);
        accountRepository.save(cuentaCarlos);

        Card tarjetaJuan = new Card();
        tarjetaJuan.setId(1L);
        tarjetaJuan.setCardNumber("1234567812345678");
        tarjetaJuan.setCardHolder("Juan Barberan");
        tarjetaJuan.setExpirationDate("12/27");
        tarjetaJuan.setCvv("123");
        tarjetaJuan.setType("DEBIT");
        tarjetaJuan.setAccount(cuentaJuan);
        cardRepository.save(tarjetaJuan);

        Transaction recarga = Transaction.builder()
                .accountId(cuentaJuan.getId())
                .amount(1000.0)
                .type("RECARGA")
                .date(LocalDateTime.of(2025, 8, 29, 10, 0))
                .description("Recarga desde tarjeta")
                .build();
        transactionRepository.save(recarga);

        for (int i = 0; i < 3; i++) {
            Transaction transferencia = Transaction.builder()
                    .accountId(cuentaJuan.getId())
                    .amount(300.0)
                    .type("EGRESO")
                    .date(LocalDateTime.of(2025, 8, 30, 1, 35).plusMinutes(i))
                    .description("Transferencia de prueba")
                    .destination("pozo.volcán.cascada")
                    .destinationType("alias")
                    .build();
            transactionRepository.save(transferencia);
        }

        System.out.println("✅ Datos precargados en account-service");
    }
}