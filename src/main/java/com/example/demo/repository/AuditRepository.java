package com.example.demo.repository;

import com.example.demo.document.BettingLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends ElasticsearchRepository<BettingLog, String> {
    // This gives us methods like .save() and .findAll().
}