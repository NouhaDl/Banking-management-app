package com.example.Project2.servicesimpl;

import com.example.Project2.repository.UserRepository;
import com.example.Project2.dto.*;
import com.example.Project2.entity.Role;
import com.example.Project2.entity.User;
import com.example.Project2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service

public class  UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

  @Override
    public BankResponse createAccount(UserRequest userRequest){
      if(userRepository.existsByEmail(userRequest.getEmail())){
          return new BankResponse().builder()
                  .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
      }
  }



}