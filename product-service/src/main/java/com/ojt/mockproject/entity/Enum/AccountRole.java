package com.ojt.mockproject.entity.Enum;

import com.ojt.mockproject.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AccountRole implements GrantedAuthority {

    private AccountRoleEnum accountRoleEnum;
    @Override
    public String getAuthority() {
        return "ROLE_" + accountRoleEnum.toString();
    }
}
