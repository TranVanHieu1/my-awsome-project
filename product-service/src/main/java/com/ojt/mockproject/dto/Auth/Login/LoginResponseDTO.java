package com.ojt.mockproject.dto.Auth.Login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.entity.Enum.AccountGenderEnum;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private String message;

    private String error;

    private String accessToken;

    private String refreshToken;

    public LoginResponseDTO(String message, String error, String accessToken, String refreshToken) {
        super();
        this.message = message;
        this.error = error;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
