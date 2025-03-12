package com.ojt.mockproject.dto.Account.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddWishListResponse {
    private String message;
    private String error;
    private Integer code;
    private DataResponse data;

    public AddWishListResponse(String message, String error, Integer code, DataResponse data) {
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
        private String courses;

        public DataResponse(String courses) {
            this.courses = courses;
        }
    }
}
