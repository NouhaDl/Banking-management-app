package com.example.Project2.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmailDetails {
    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;
}
