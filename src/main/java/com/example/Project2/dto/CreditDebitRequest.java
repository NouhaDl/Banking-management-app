package com.example.Project2.dto;

import lombok.*;
import java.math.BigDecimal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CreditDebitRequest {

        private String accountNumber;
        private BigDecimal amount;

    }
