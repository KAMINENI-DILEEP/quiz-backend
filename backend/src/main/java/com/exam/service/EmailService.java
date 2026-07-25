package com.exam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

     @Value("${resend.api.key:re_L1jPaGUY_LgJju8N8az3eVTnwDBjvXuMq}")
private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, String subject, String body) {
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> emailPayload = new HashMap<>();
        emailPayload.put("from", "onboarding@resend.dev");
        emailPayload.put("to", new String[]{to});
        emailPayload.put("subject", subject);
        emailPayload.put("html", "<p>" + body.replace("\n", "<br>") + "</p>");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email sent successfully to: " + to);
            } else {
                System.err.println("Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Exception while sending email via Resend API: " + e.getMessage());
        }
    }
}
