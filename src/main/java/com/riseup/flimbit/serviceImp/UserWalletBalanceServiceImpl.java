package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.UserWalletBalance;
import com.riseup.flimbit.repository.UserWalletBalanceRepository;
import com.riseup.flimbit.service.UserWalletBalanceService;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
public class UserWalletBalanceServiceImpl implements UserWalletBalanceService {

	@Autowired
     UserWalletBalanceRepository walletRepo;

  

    @Override
    public UserWalletBalance getOrCreateWallet(int userId) {
        return walletRepo.findByUserId(userId).orElseGet(() -> {
            UserWalletBalance wallet = UserWalletBalance.builder()
                    .userId(userId)
                    .shareCashBalance(BigDecimal.ZERO)
                    .build();
            return walletRepo.save(wallet);
        });
    }

    @Override
    public void addShareCash(int userId, BigDecimal amount) {
        UserWalletBalance wallet = getOrCreateWallet(userId);
        wallet.setShareCashBalance(wallet.getShareCashBalance().add(amount));
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        walletRepo.save(wallet);
    }

    @Override
    public void deductShareCash(int userId, BigDecimal amount) {
        UserWalletBalance wallet = getOrCreateWallet(userId);
        if (wallet.getShareCashBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }
        wallet.setShareCashBalance(wallet.getShareCashBalance().subtract(amount));
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        walletRepo.save(wallet);
    }

    @Override
    public BigDecimal getBalance(int userId) {
        return getOrCreateWallet(userId).getShareCashBalance();
    }
}

