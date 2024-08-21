package com.example.Project2.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private Double amount;
}
