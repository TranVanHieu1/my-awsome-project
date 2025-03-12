package com.ojt.mockproject.dto.Google;

import com.ojt.mockproject.entity.Enum.AccountRoleEnum;

public class GoogleResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private AccountRoleEnum roles; // Or List<String> if the user can have multiple roles
    private String token;
}
