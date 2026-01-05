package com.example.demo.service;

import com.example.demo.model.PlayerWallet;
import com.example.demo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private WalletRepository repository;

    // value="wallets" is the name of the cache bucket
    // key="#username" uses the player's name as the ID in Redis
    @Cacheable(value = "wallets", key = "#username")
    public PlayerWallet getOrCreateWallet(String username) {
        // prove cache is working
        System.out.println("--- 🏦 DATABASE HIT for: " + username + " ---");

        return repository.findByUsername(username)
                .orElseGet(() -> {
                    PlayerWallet newWallet = new PlayerWallet();
                    newWallet.setUsername(username);
                    newWallet.setBalance(new BigDecimal("1000.00"));
                    return repository.save(newWallet);
                });
    }
}