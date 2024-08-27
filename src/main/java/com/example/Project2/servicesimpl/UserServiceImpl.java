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
    public BankResponse createAccount(UserRequest userRequest) {
      if (userRepository.existsByEmail(userRequest.getEmail())) {
          return new BankResponse().builder()
                  .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                  .responseCode(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                  .accountInfo(null)
                  .build();
      }

      User newUser = User.builder()
              .firstName(userRequest.getFirstname())
              .lastName(userRequest.getLastname())
              .gender(userRequest.getGender())
              .address(userRequest.getAdress())
              .stateOfOrigin(userRequest.getStateOfOrigin())
              .accountNumber(AccountUtils.generateAccountNumber())
              .accountBalance(BigDecimal.ZERO)
              .email(userRequest.getEmail())
              .password(passwordEncoder.encode(userRequest.getPassword()))
              .phoneNumber(userRequest.getPhoneNumber())
              .alternativeNumber(userRequest.getAlternativePhoneNumber())
              .status("ACTIVE")
              .role(Role.valueOf("ROLE_ADMIN"))
              .build();

      User savedUser = userRepository.save(newUser);
      //Send an email notif abt creation of account
      EmailDetails emailDetails = EmailDetails.builder()
              .recipient(savedUser.getEmail())
              .subject("NEW ACCOUNT CREATED")
              .messageBody("CONGRATULATIONS , your account has been successfully created . \n Your Account Details: \n Account Name:" + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n Account Number: " + savedUser.getAccountNumber())
              .build();
  }
  public BankResponse login(LoginDto loginDto){
      Authentication authentication= authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
      );

      EmailDetails loginAlert= EmailDetails.builder()
              .subject("Your'e logged in!")
              .recipient(loginDto.getEmail())
              .messageBody("You logged into your account, if you did not initiate this request please contact Your bank ")
              .build();
      emailService.SendEmailAlert(loginAlert);
      return BankResponse.builder()
              .responseCode("Login success")
              .responseMessage(jwtTokenProvider.generateToken(authentication))
}

@Override
public BankResponse balanceEnquiry(EnquiryResquest request){
    // check if the given account number exists
    boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!isAccountExist){
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
    }
    User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
    return BankResponse.builder()
            .responseCode(ccountUtils.ACCOUNT_FOUND_CODE)
            .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
            .accountInfo(AccountInfo.builder()
                    .accountBalance(foundUser.getAccountBalance().toString())
                    .accountName(foundUser.getFirstName()+" " + foundUser.getLastName())
                    .accountNumber(request.getAccountNumber())
                    .build())
            .build();
  }
  @Override
    public String nameEnquiry(EnquiryResquest resquest){
      boolean isAccountExist = userRepository.existsByAccountNumber(resquest.getAccountNumber());
      if(!isAccountExist){
          return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
      }
      User foundUser = userRepository.findByAccountNumber(resquest.getAccountNumber());
      return foundUser.getFirstName() + " " + foundUser.getLastName();
  }

  @Override
  public BankResponse creditAccount(CreditDebitRequest request) {
      //checking if account exists
      boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
      if (!isAccountExist) {
          return BankResponse.builder()
                  .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                  .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                  .accountInfo(null)
                  .build();
      }
      User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
      userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
      userRepository.save(userToCredit);
      //Logic to save the transaction
      TransactionDto transactionDto = TransactionDto.builder()
              .accountNumber(userToCredit.getAccountNumber())
              .transactionType("CREDIT")
              .amount(request.getAmount())
              .status("SUCCESS")
              .build();
      transactionService.saveTransaction(transactionDto);


      return BankResponse.builder()
              .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
              .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
              .accountInfo(AccountInfo.builder()
                      .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                      .accountBalance(userToCredit.getAccountBalance().toString())
                      .accountNumber(request.getAccountNumber())
                      .build())
              .build();
  }
@Override
public BankResponse debitAccount(CreditDebitRequest request){
    //check if account exists
    boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!isAccountExist) {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
  }
    User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
    BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
    BigInteger debitAmount = request.getAmount().toBigInteger();
    if(availableBalance.compareTo(debitAmount)<0){
        return BankResponse.builder()
                .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                .accountInfo(null)
                .build();
    }
    else{
        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToDebit.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .status("SUCCESS")
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(request.getAccountNumber())
                        .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                        .accountBalance(userToDebit.getAccountBalance().toString())
                        .build())
                .build();
    }

    }
    @Override
    public BankResponse transferAccount(TransferRequest request){
      boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());

        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

            User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
            if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance().doubleValue())>0){
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();

            }
            //Source account user logic
            sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(BigDecimal.valueOf(request.getAmount())));
            userRepository.save(sourceAccountUser);
            EmailDetails debitAlert = EmailDetails.builder()
                    .subject("DEBIT ALERT")
                    .recipient(sourceAccountUser.getEmail())
                    .messageBody("A sum of " + request.getAmount() + "has been deducted from your account! Your current balance" + sourceAccountUser.getAccountBalance())
                    .build();

            emailService.SendEmailAlert(debitAlert);

            // Destination account User logic
            User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
            destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(BigDecimal.valueOf(request.getAmount())));
            userRepository.save(destinationAccountUser);
            //Sending the mail
            EmailDetails creditAlert = EmailDetails.builder()
                    .subject("CREDIT ALERT")
                    .recipient(destinationAccountUser.getEmail())
                    .messageBody("A sum of" + request.getAmount() + "has been credited to your account from" + sourceAccountUser.getAccountNumber() + "Your current balance " + destinationAccountUser.getAccountBalance())
                    .build();
            emailService.SendEmailAlert(creditAlert);

            //Logic to sav


            }
        }
        

  }

