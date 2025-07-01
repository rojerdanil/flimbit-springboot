package com.riseup.flimbit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.UserWalletBalance;
import com.riseup.flimbit.repository.UserWalletBalanceRepository;

import java.math.BigDecimal;

@Service
public class UserWalletBalanceServiceImpl implements UserWalletBalanceService {

	@Autowired
     UserWalletBalanceRepository walletRepo;

  

    @Override
    public UserWalletBalance getOrCreateWallet(Long userId) {
        return walletRepo.findByUserId(userId).orElseGet(() -> {
            UserWalletBalance wallet = UserWalletBalance.builder()
                    .userId(userId)
                    .shareCashBalance(BigDecimal.ZERO)
                    .build();
            return walletRepo.save(wallet);
        });
    }

    @Override
    public void addShareCash(Long userId, BigDecimal amount) {
        UserWalletBalance wallet = getOrCreateWallet(userId);
        wallet.setShareCashBalance(wallet.getShareCashBalance().add(amount));
        wallet.setLastUpdated(java.time.LocalDateTime.now());
        walletRepo.save(wallet);
    }

    @Override
    public void deductShareCash(Long userId, BigDecimal amount) {
        UserWalletBalance wallet = getOrCreateWallet(userId);
        if (wallet.getShareCashBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }
        wallet.setShareCashBalance(wallet.getShareCashBalance().subtract(amount));
        wallet.setLastUpdated(java.time.LocalDateTime.now());
        walletRepo.save(wallet);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        return getOrCreateWallet(userId).getShareCashBalance();
    }
}

