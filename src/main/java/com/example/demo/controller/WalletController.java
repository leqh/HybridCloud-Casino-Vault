package com.example.demo.controller;

import com.example.demo.model.PlayerWallet;
import com.example.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/casino")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/balance/{username}")
    public PlayerWallet getBalance(@PathVariable String username) {
        return walletService.getOrCreateWallet(username);
    }
}