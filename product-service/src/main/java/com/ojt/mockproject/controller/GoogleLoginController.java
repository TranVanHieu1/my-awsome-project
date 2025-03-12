package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Auth.Register.Token;
import com.ojt.mockproject.dto.Google.GoogleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/login/oauth2/code/google")
@CrossOrigin("*")
public class GoogleLoginController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    //Redirect Google AccessToken to AuthController to handle
    @GetMapping
    public void getLoginInfo(@AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        // Send accessToken to your API "login/google"
        RestTemplate restTemplate = new RestTemplate();
        GoogleResponseDTO response = restTemplate.postForObject("http://localhost:8080/auth/login/google", new Token(accessToken), GoogleResponseDTO.class);

    }

}
