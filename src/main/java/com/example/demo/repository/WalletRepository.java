package com.example.demo.repository;

import com.example.demo.model.PlayerWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<PlayerWallet, Long> {
    // This magic method allows us to find a player by their name!
    Optional<PlayerWallet> findByUsername(String username);
}
