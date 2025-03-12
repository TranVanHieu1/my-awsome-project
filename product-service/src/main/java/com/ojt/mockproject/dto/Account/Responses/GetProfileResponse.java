package com.ojt.mockproject.dto.Account.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProfileResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private AccountRoleEnum accountRole;
    private String avatar;
    private String aboutMe;

    public GetProfileResponse(Integer id, String name, String email, String phone, AccountRoleEnum accountRole, String avatar, String aboutMe) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.accountRole = accountRole;
        this.avatar = avatar;
        this.aboutMe = aboutMe;
    }
}