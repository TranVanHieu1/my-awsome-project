package com.ojt.mockproject.exceptionhandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
//    ACCOUNTS | CODE: 1XXX
//    Accounts | Auth
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "User name must be at least 3 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1006, "Invalid token", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_VERIFY(1007, "This account has not been verified", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(1008, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    OLD_PASSWORD_INCORRECT(1009, "Old password incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_REPEAT_INCORRECT(1010, "Password repeat do not match", HttpStatus.BAD_REQUEST),
    NOT_LOGIN(1011, "You need to login", HttpStatus.BAD_REQUEST),
    USERNAME_PASSWORD_NOT_CORRECT(1012, "Username or password is not correct", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(1013,"Account not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(1013,"Email not found, please register account.", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_INSTRUCTOR(1014,"Account not instructor", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_STUDENT(1015,"Account not student", HttpStatus.BAD_REQUEST),
    ACCOUNT_IS_DELETED(1016,"This account has been deleted", HttpStatus.BAD_REQUEST),

//    Accounts | Emails | CODE: 15XX
    INVALID_EMAIL(1500, "Invalid email", HttpStatus.BAD_REQUEST),
    EMAIL_WAIT_VERIFY(1501, "This email has been registered and is not verified, please verify and login", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1502, "This email has been registered, please log in!", HttpStatus.BAD_REQUEST),
    SUCCESS(200, "Success",HttpStatus.OK),

//    COURSES | CODE: 2XXX
    COURSE_NOT_FOUND(2001, "Course not found", HttpStatus.NOT_FOUND),
    INVALID_ENUM(2002, "Invalid enum format", HttpStatus.BAD_REQUEST),
    NO_COURSE_IN_WISHLIST(2003, "No courses in wishlist", HttpStatus.BAD_REQUEST),
    INVALID_COURSE_ID(2004, "Invalid course ID", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND_IN_WISHLIST(2005, "Course not found in wishlist", HttpStatus.BAD_REQUEST),
    COURSE_IS_DELETED(2006,"This Course has been deleted", HttpStatus.BAD_REQUEST),
//    WALLETS | CODE: 3XXX
    WALLET_NOT_FOUND(3000, "This wallet does not exist, please re-check", HttpStatus.BAD_REQUEST),
    WALLET_LOG_NOT_FOUND(3001, "There is no such wallet log, please-recheck", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_MATCH(3002, "Account not match.", HttpStatus.BAD_REQUEST),
    CANCELLED(3003, "Cancelled", HttpStatus.BAD_REQUEST),
    MONTH_WITHOUT_WALLET_LOG(3004, "This are no wallet log this month", HttpStatus.BAD_REQUEST),
    WALLET_LOG_IS_DELETED(5001,"This Order has been deleted", HttpStatus.BAD_REQUEST),
//    CATEGORIES | CODE: 4XXX
    NO_CATEGORY_FOUND(4000, "There is no such category, please-recheck", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY(4001, "Invalid category, please recheck", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(4002, "Invalid input, please recheck", HttpStatus.BAD_REQUEST),

//    ORDERS | CODE: 5XXX
    ORDER_NOT_FOUND(5000, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_IS_DELETED(5001,"This Order has been deleted", HttpStatus.BAD_REQUEST),

//    WALLETS | CODE: 6XXX
    PRICE_INVALID(6000, "Price must be greater than 0", HttpStatus.BAD_REQUEST),
    BALANCE_INVALID(6001, "Amount must be greater than 0 and less than the remaining balance.", HttpStatus.BAD_REQUEST),
    BALANCE_NOT_ENOUGH(6002, "Doesn't have enough balance", HttpStatus.BAD_REQUEST),
    // TRANSACTIONS | CODE: 7XXX
    TRANSACTION_NOT_FOUND(7000, "Transaction not found", HttpStatus.NOT_FOUND),

//    CERTIFICATE | CODE: 7XXX
    CERTIFICATE_NOT_FOUND(7000, "This wallet does not exist, please re-check", HttpStatus.BAD_REQUEST),
    ;
    @Getter
    private final Integer code;
    @Setter
    private String message;
    @Getter
    private final HttpStatus httpStatus;

    ErrorCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
