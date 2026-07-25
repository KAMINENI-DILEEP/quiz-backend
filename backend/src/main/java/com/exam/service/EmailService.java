package com.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String API_URL = "https://api.brevo.com/v3/smtp/email";
    private final String API_KEY = "your-brevo-api-key"; // Get a free key from brevo.com

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", API_KEY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", new HashMap<>(Map.of("name", "Exam Portal", "email", "your-verified-email@domain.com")));
            requestBody.put("to", List.of(new HashMap<>(Map.of("email", toEmail))));
            requestBody.put("subject", subject);
            requestBody.put("textContent", body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(API_URL, entity, String.class);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via HTTP API: " + e.getMessage());
        }
    }
}
