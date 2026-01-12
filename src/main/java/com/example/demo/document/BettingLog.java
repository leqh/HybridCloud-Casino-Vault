package com.example.demo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(indexName = "bet-history") // Creates an index named "bet-history"
public class BettingLog {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Double)
    private BigDecimal betAmount;

    @Field(type = FieldType.Keyword)
    private String result; // "WIN" or "LOSS"

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;
}