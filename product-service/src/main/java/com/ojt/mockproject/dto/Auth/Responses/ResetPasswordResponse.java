package com.ojt.mockproject.dto.Auth.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetPasswordResponse {
    private String message;
    private String error;
    private Integer code;

    public ResetPasswordResponse(String message, String error, Integer code) {
        super();
        this.message = message;
        this.error = error;
        this.code = code;
    }
}
