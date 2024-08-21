package com.example.Project2.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BankResponse {

    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;

}
