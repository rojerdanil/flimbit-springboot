package com.riseup.flimbit.service;


import java.math.BigDecimal;

import com.riseup.flimbit.entity.UserWalletBalance;

public interface UserWalletBalanceService {
    UserWalletBalance getOrCreateWallet(int userId);
    void addShareCash(int i, BigDecimal amount);
    void deductShareCash(int userId, BigDecimal amount);
    BigDecimal getBalance(int userId);
    
}
