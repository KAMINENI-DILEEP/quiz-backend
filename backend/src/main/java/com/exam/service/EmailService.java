package com.exam.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final String API_URL = "https://api.brevo.com/v3/smtp/email";
    private final String API_KEY = "your-brevo-api-key";

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
