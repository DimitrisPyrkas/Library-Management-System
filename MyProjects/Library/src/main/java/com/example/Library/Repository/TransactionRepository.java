package com.example.Library.Repository;

import com.example.Library.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//TransactionRepository handles the data access operations for the Transaction Entity

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
}
