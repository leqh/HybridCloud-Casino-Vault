package com.example.demo.controller;

import com.example.demo.dto.BetRequest;
import com.example.demo.model.PlayerWallet;
import com.example.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/casino")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // 1. Check Balance (GET)
    @GetMapping("/balance/{username}")
    public PlayerWallet getBalance(@PathVariable String username) {
        return walletService.getOrCreateWallet(username);
    }

    // 2. Place Bet (POST)
    @PostMapping("/bet")
    public PlayerWallet placeBet(@RequestBody BetRequest request) {
        return walletService.placeBet(request.getUsername(), request.getBetAmount());
    }
}