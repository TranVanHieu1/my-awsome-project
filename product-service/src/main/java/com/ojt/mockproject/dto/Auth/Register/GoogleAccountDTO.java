package com.ojt.mockproject.dto.Auth.Register;

import lombok.*;

@Getter
@Setter// Getter Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAccountDTO {

    Integer id;
    String email;
    String fullName;
    String picture;
    String username;
    String password;

    public GoogleAccountDTO(String email, String fullName, String picture) {
        this.email = email;
        this.fullName = fullName;
        this.picture = picture;
    }

}
