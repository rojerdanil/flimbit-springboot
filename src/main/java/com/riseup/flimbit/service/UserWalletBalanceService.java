package com.riseup.flimbit.service;


import java.math.BigDecimal;

import com.riseup.flimbit.entity.UserWalletBalance;

public interface UserWalletBalanceService {
    UserWalletBalance getOrCreateWallet(Long userId);
    void addShareCash(Long userId, BigDecimal amount);
    void deductShareCash(Long userId, BigDecimal amount);
    BigDecimal getBalance(Long userId);
    
}
