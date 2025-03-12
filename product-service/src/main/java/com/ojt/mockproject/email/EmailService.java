package com.ojt.mockproject.email;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.service.JWTService;
import com.ojt.mockproject.service.AccountService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Value("${public.api.url}")
    private String publicApiUrl;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JWTService jwtService;


    public void sendVerifyAccountMailTemplate(Account account) {
        try {
            System.out.println("Received account: " + account);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(account.getName());
            emailDetail.setRecipient(account.getEmail());
            emailDetail.setSubject("Congratulation!");
            emailDetail.setMsgBody("Your account has been verified!");
            VerifyAccountMailTemplate(emailDetail);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendForgotPasswordEmail(Account account) {
        try {
            String token = account.getTokens();
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setName(account.getName());
            emailDetail.setRecipient(account.getEmail());
            emailDetail.setSubject("Forgot Password Request");

            // Construct the link for password reset
            String link = publicApiUrl + "/auth/reset-password?token=" + token;
            emailDetail.setMsgBody(link);
            // Send the email using the email template method
            sendEmailWithTemplate(emailDetail, "forgotPasswordEmailTemplate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmailWithTemplate(EmailDetail emailDetail, String templateName) {
        try {
            // Create a Thymeleaf context
            Context context = new Context();
            context.setVariable("name", emailDetail.getName());
            context.setVariable("link", emailDetail.getMsgBody());

            // Process the Thymeleaf template
            String text = templateEngine.process(templateName, context);

            // Create a MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            // Set email details
            mimeMessageHelper.setFrom("contact.us.nicetrip@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(text, true);

            // Send the email
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void VerifyAccountMailTemplate(EmailDetail emailDetail){
        try{
            Context context = new Context();

            context.setVariable("name", emailDetail.getName());
            String token = jwtService.generateToken(emailDetail.getRecipient());
            String link = "http://localhost:8080/auth/verify/" + token;

            context.setVariable("link", link);


            String text = templateEngine.process("sendVerifyEmail", context);

            // Creating a simple mail message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("nguyenleminhdung2912@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }


}
