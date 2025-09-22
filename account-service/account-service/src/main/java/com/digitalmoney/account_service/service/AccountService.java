package com.digitalmoney.account_service.service;
import com.digitalmoney.account_service.dto.AccountDTO;

public interface AccountService {
    Double getBalanceByUserId(Long userId);
    void createAccount(Long userId, AccountDTO dto);
    AccountDTO getAccountProfile(Long userId);
    Long getUserIdByEmail(String email);
}