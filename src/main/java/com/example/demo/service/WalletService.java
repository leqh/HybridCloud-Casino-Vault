package com.example.demo.service;

import com.example.demo.model.PlayerWallet;
import com.example.demo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private WalletRepository repository;

    // read operation (fast)
    // value="wallets" is the name of the cache bucket
    // key="#username" uses the player's name as the ID in Redis
    @Cacheable(value = "wallets", key = "#username")
    public PlayerWallet getOrCreateWallet(String username) {
        // prove cache is working
        System.out.println("--- DATABASE HIT for: " + username + " ---");

        return repository.findByUsername(username)
                .orElseGet(() -> {
                    PlayerWallet newWallet = new PlayerWallet();
                    newWallet.setUsername(username);
                    newWallet.setBalance(new BigDecimal("1000.00"));
                    return repository.save(newWallet);
                });
    }

    // --- WRITE OPERATION (Secure) ---
    // @Transactional: If line 45 fails, line 35 is undone (Money is returned)
    // @CacheEvict: Deletes the OLD balance from Redis so the next read gets the NEW balance
    @Transactional
    @CacheEvict(value = "wallets", key = "#username")
    public PlayerWallet placeBet(String username, BigDecimal betAmount) {
        // 1. Get current wallet (Force DB fetch to be safe)
        PlayerWallet wallet = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Check if they have enough money
        if (wallet.getBalance().compareTo(betAmount) < 0) {
            throw new RuntimeException("Insufficient Funds! You are broke.");
        }

        // 3. Deduct the bet
        BigDecimal newBalance = wallet.getBalance().subtract(betAmount);

        // 4. THE GAME LOGIC (Simple 50/50 Coin Flip)
        // If random number > 0.5, they Win 2x their bet.
        boolean isWin = Math.random() > 0.5;
        if (isWin) {
            BigDecimal winnings = betAmount.multiply(new BigDecimal("2"));
            newBalance = newBalance.add(winnings);
        }

        // 5. Save the new balance
        wallet.setBalance(newBalance);
        return repository.save(wallet);
    }
}