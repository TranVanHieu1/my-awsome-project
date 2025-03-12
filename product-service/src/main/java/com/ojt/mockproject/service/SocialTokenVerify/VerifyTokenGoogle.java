package com.ojt.mockproject.service.SocialTokenVerify;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ojt.mockproject.dto.Auth.Register.GoogleAccountDTO;
import com.ojt.mockproject.exceptionhandler.InvalidToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class VerifyTokenGoogle {

// 1. Valid token => return account
// 2. Token format is in correct but wrong payload
// 3. Token format is not correct
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList("467879247851-l519h15u5tcv3iucjuan6534cr43j7ba.apps.googleusercontent.com"))
            .build();

    public GoogleAccountDTO verifyToken(String token){
        try{
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                GoogleAccountDTO googleAccountDTO = new GoogleAccountDTO(email, name, pictureUrl);
                return googleAccountDTO;
            } else {
                throw new InvalidToken("Token invalid!!!");
            }
        }catch (GeneralSecurityException | IOException e){
            return null;
        } catch (IllegalArgumentException illegalArgumentException){
            // Failed to verify
            throw new InvalidToken("Malformed token!!!");
        }
    }
}
