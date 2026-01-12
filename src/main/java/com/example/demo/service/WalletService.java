package com.example.demo.service;

import com.example.demo.document.BettingLog;
import com.example.demo.model.PlayerWallet;
import com.example.demo.repository.AuditRepository;
import com.example.demo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private AuditRepository auditRepository;

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
        // Get current wallet (Force DB fetch to be safe)
        PlayerWallet wallet = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if they have enough money
        if (wallet.getBalance().compareTo(betAmount) < 0) {
            throw new RuntimeException("Insufficient Funds! You are broke.");
        }

        // Deduct the bet
        BigDecimal newBalance = wallet.getBalance().subtract(betAmount);

        // THE GAME LOGIC (Simple 50/50 Coin Flip)
        // If random number > 0.5, they Win 2x their bet.
        boolean isWin = Math.random() > 0.5;
        String result = "LOSS";

        if (isWin) {
            BigDecimal winnings = betAmount.multiply(new BigDecimal("2"));
            newBalance = newBalance.add(winnings);
            result = "WIN";
        }

        // Save to Postgres
        wallet.setBalance(newBalance);
        PlayerWallet savedWallet = repository.save(wallet);

        // Audit Log
        try {
            BettingLog log = new BettingLog();
            log.setId(UUID.randomUUID().toString());
            log.setUsername(username);
            log.setBetAmount(betAmount);
            log.setResult(result);
            log.setTimestamp(LocalDateTime.now());

            auditRepository.save(log);
            System.out.println("Audit Log saved for: " + username);
        } catch (Exception e) {
            System.err.println("Audit Log Failed: " + e.getMessage());
        }
        return savedWallet;
    }
}