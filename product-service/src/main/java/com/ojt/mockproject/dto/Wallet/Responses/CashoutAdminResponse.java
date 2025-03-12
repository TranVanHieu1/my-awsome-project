package com.ojt.mockproject.dto.Wallet.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashoutAdminResponse {
    private CashOutResponse.DataResponse dataResponse;
    private String emailAdmin;

    public CashoutAdminResponse(CashOutResponse.DataResponse dataResponse, String emailAdmin) {
        this.dataResponse = dataResponse;
        this.emailAdmin = emailAdmin;
    }
}
