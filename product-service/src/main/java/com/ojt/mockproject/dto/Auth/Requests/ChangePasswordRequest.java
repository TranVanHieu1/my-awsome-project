package com.ojt.mockproject.dto.Auth.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String password;
    private String old_password;
    private String new_password;
    private String repeat_password;
}
