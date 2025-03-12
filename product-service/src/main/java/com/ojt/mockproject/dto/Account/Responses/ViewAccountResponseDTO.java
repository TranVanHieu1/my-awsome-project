package com.ojt.mockproject.dto.Account.Responses;

import com.ojt.mockproject.entity.Enum.AccountGenderEnum;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewAccountResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountGenderEnum gender;
    private String avatar;
    private AccountRoleEnum role;
    private AccountStatusEnum status;
    // Constructor

}
