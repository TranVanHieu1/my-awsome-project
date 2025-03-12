package com.ojt.mockproject.dto.Wallet;
import com.ojt.mockproject.dto.Wallet.Requests.BuyCourseRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class WalletValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateWalletRequest.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        CreateWalletRequest request = (CreateWalletRequest) target;

        if (request.getBankName() == null || request.getBankName().isEmpty()) {
            errors.rejectValue("bankName", "bankName.null", "Bank Name cannot be null or empty");
        }

        if (request.getBankAccountNumber() == null || request.getBankAccountNumber().isEmpty()) {
            errors.rejectValue("bankAccountNumber", "bankAccountNumber.null", "Bank Account Number cannot be null or empty");
        }
    }
}
