package com.ojt.mockproject.service.SocialTokenVerify;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

@Service
public class VerifyTokenFacebook {

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String facebookAppId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String facebookAppSecret;

    public ResponseEntity<?> authenticate(Map<String, String> body) {
        String accessToken = body.get("token");

        // Check token with facebook graph api
        String url = "https://graph.facebook.com/debug_token?input_token=" + accessToken +
                "&access_token=" + facebookAppId + "|" + facebookAppSecret;

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            boolean isValid = (boolean) data.get("is_valid");

            if (isValid && facebookAppId.equals(data.get("app_id"))) {
                String userId = (String) data.get("user_id");

                // Take user's info from Facebook Graph API
                String userInfoUrl = "https://graph.facebook.com/me?access_token=" + accessToken + "&fields=id,name,email";
                Map<String, Object> userInfo = restTemplate.getForObject(userInfoUrl, Map.class);

                if (userInfo != null) {
                    String email = (String) userInfo.get("email");
                    String name = (String) userInfo.get("name");

                    // Return statement
                    return ResponseEntity.ok("User authenticated successfully");
                } else {
                    return ResponseEntity.status(401).body("Unable to fetch user info.");
                }
            } else {
                return ResponseEntity.status(401).body("Invalid access token.");
            }
        } else {
            return ResponseEntity.status(401).body("Invalid access token.");
        }
    }

}
