package com.ojt.mockproject.controller;

import com.ojt.mockproject.email.EmailDetail;
import com.ojt.mockproject.email.EmailService;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class MailController {

    @Autowired
    EmailService emailService;
    @Autowired
    AccountService accountService;

    @GetMapping("/test/sendMail")
    public void sendMail(Account accountEntity){
        try {
            EmailDetail emailDetail = new EmailDetail();
//            emailDetail.setRecipient(accountEntity.getEmail());
//            emailDetail.setName(accountEntity.getName());
            emailDetail.setRecipient("nguyenleminhdung2912@gmail.com");
            emailDetail.setSubject("Verify account");
            emailDetail.setMsgBody("Please verify your account");
            emailDetail.setName("Minh Dung");
            emailService.VerifyAccountMailTemplate(emailDetail);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
