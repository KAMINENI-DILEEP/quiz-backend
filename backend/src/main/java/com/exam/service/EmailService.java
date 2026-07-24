package com.exam.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendEmail(String to, String subject, String body) {
        try {
            Resend resend = new Resend(resendApiKey);

            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .from("onboarding@resend.dev")
                    .to(to)
                    .subject(subject)
                    .html("<p>" + body.replace("\n", "<br>") + "</p>")
                    .build();

            SendEmailResponse data = resend.emails().send(sendEmailRequest);
            System.out.println("Email sent successfully, ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Failed to send email via Resend SDK: " + e.getMessage());
        }
    }
}
