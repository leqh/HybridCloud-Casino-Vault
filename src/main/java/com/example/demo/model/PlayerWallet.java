package com.example.demo.model; // Adjust to your actual package name

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Data
public class PlayerWallet implements Serializable {
    private static final long serialVersionUID = 1L; // Version control for serialization

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private BigDecimal balance; // BigDecimal is best for money
}