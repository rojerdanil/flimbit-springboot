package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.service.UserWalletBalanceService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class UserWalletBalanceController {

	@Autowired
    UserWalletBalanceService walletService;

  
    @GetMapping("/{userId}")
    public BigDecimal getBalance(@PathVariable int userId) {
        return walletService.getBalance(userId);
    }

    @PostMapping("/{userId}/add")
    public void addCash(@PathVariable int userId, @RequestParam BigDecimal amount) {
        walletService.addShareCash(userId, amount);
    }

    @PostMapping("/{userId}/deduct")
    public void deductCash(@PathVariable int userId, @RequestParam BigDecimal amount) {
        walletService.deductShareCash(userId, amount);
    }
}
