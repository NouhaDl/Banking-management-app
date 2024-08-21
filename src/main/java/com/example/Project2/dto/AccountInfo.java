package com.example.Project2.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AccountInfo {

    private String accountName;
    private String accountBalance;
    private String accountNumber;
}
