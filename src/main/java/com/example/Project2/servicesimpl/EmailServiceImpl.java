package com.example.Project2.servicesimpl;

import com.example.Project2.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void SendEmailAlert(EmailDetails emailDetails){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent Successfuly");
        }catch (MailException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMailMessageHelper;
        try{
            mimeMailMessageHelper=new MimeMessageHelper(mimeMessage,true);
            mimeMailMessageHelper.setFrom(senderEmail);
            mimeMailMessageHelper.setTo(emailDetails.getRecipient());
            mimeMailMessageHelper.setText(emailDetails.getMessageBody());
            mimeMailMessageHelper.setSubject(emailDetails.getSubject());

            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMailMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()),file);
            javaMailSender.send(mimeMessage);

            log.info(file.getFilename()+"has been sent to user with email"+ emailDetails.getRecipient());
        }catch (MessagingException e){
            throw new RuntimeException(e);
        }

    }





}
