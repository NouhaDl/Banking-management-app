package com.example.Project2.servicesimpl;

import com.example.Project2.dto.*;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryResquest request);
    String nmaeEnquiry(EnquiryResquest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transferAccount(TransferRequest request);
    BankResponse login(LoginDto loginDto);
}
