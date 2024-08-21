package com.example.Project2.repository;

import com.example.Project2.dto.TransactionDto;
import com.example.Project2.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transaction,String> {
}
