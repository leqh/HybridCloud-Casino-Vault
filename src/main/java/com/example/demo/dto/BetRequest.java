package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BetRequest {
    // The player placing the bet
    private String username;

    // How much they are risking
    private BigDecimal betAmount;
}