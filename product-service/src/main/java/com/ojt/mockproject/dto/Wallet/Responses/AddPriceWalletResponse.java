package com.ojt.mockproject.dto.Wallet.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.entity.WalletLog;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPriceWalletResponse {
    private String message;
    private String error;
    private Integer code;
    private DataResponse data;

    public AddPriceWalletResponse(String message, String error, Integer code, DataResponse data) {
        this.message = message;
        this.error = error;
        this.code = code;
        this.data = data;
    }

    @Getter
    @Setter
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataResponse {
        private Integer id;
        private BigDecimal balance;
        private BigDecimal addBalance;
        private WalletLog walletLog;
        private String name;
        private String email;

        public DataResponse(Integer id, BigDecimal balance, BigDecimal addBalance, WalletLog walletLog, String name, String email) {
            this.id = id;
            this.balance = balance;
            this.addBalance = addBalance;
            this.walletLog = walletLog;
            this.name = name;
            this.email = email;
        }
    }
}
