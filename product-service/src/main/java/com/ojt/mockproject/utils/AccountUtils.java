package com.ojt.mockproject.utils;

import com.ojt.mockproject.entity.Account;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {

    public Account getCurrentAccount(){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (object instanceof Account) {
            return (Account) object;
        } else {
            // Return null if the principal is not an instance of Account
            return null;
        }
    }
}