package com.example.Project2.servicesimpl;

import com.example.Project2.dto.EmailDetails;

public interface EmailService {

    void SendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
