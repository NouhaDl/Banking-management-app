package com.example.Project2.servicesimpl;

import com.example.Project2.dto.TransactionDto;
import com.example.Project2.entity.Transaction;
import com.example.Project2.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto){
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status(transactionDto.getStatus())
                .build();
        transactionRepository.save(transaction);
        System.out.println(("Transaction saved successfully!"));


    }

}
