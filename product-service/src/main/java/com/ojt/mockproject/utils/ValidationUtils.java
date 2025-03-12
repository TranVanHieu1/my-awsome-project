package com.ojt.mockproject.utils;

import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Orderr;
import com.ojt.mockproject.entity.WalletLog;
import com.ojt.mockproject.exceptionhandler.AuthAppException;
import com.ojt.mockproject.exceptionhandler.ValidationException;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletLogException;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.order.OrderException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ValidationUtils {

    public static void validatePhone(String phone) {
        if (!Pattern.matches("^\\d{10}$", phone)) {
            throw new ValidationException("Invalid phone number: " + phone, ErrorCode.INVALID_INPUT);
        }
    }
    public static void validateUniqueCourseIds(List<Integer> courses) {
        if (courses == null || courses.isEmpty()) {
            return;
        }

        Set<Integer> courseSet = new HashSet<>(courses);
        if (courseSet.size() != courses.size()) {
            throw new ValidationException("Course IDs must be unique", ErrorCode.INVALID_INPUT);
        }
    }

    public static void validateEmail(String email) {
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            throw new AuthAppException(ErrorCode.INVALID_EMAIL);
        }
    }

    public static void validatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid price: " + price, ErrorCode.INVALID_INPUT);
        }
    }

    public static void validatePriceWallet(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Invalid price: " + price, ErrorCode.INVALID_INPUT);
        }
    }

    public static void validateAccount(Integer accountId, boolean accountExists) {
        if (!accountExists) {
            throw new ValidationException("Account not found with id: " + accountId, ErrorCode.USER_NOT_FOUND);
        }
    }
    public static void validateAccountIsDeleted(Account account){
        if(account.getIsDeleted()){
            throw new AccountException("This account has been deleted", ErrorCode.ACCOUNT_IS_DELETED);
        }
    }
    public static void validateCourseIsDeleted(Course course){
        if(course.getIsDeleted()){
            throw new CourseException("This course has been deleted", ErrorCode.COURSE_IS_DELETED);
        }
    }
    public static void validateOrderIsDeleted(Orderr order){
        if(order.getIsDeleted()){
            throw new OrderException("This order has been deleted", ErrorCode.ORDER_IS_DELETED);
        }
    }
    public static void validateWalletLogIsDeleted(WalletLog walletLog){
        if(walletLog.getIsDeleted()){
            throw new WalletLogException("This walletLog has been deleted", ErrorCode.WALLET_LOG_IS_DELETED);
        }
    }
}