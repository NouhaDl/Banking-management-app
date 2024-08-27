package com.example.Project2.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserRequest {

    private Long id;
    private String firstname;
    private String lastname;
    private String gender;
    private String adress;
    private String stateOfOrigin;
    private String email;
    private String password;
    private String phoneNumber;
    private String alternativePhoneNumber;
}
