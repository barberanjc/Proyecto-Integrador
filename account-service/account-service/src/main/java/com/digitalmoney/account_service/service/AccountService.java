package com.digitalmoney.account_service.service;

public interface AccountService {
    Double getBalanceByUserId(Long userId);
    void createAccount(Long userId);

}