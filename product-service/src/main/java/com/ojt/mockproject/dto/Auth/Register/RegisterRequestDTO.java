package com.ojt.mockproject.dto.Auth.Register;

import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private String email;
    private String password;
    String name;
    AccountRoleEnum accountRoleEnum;
}
