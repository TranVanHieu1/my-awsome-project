package com.ojt.mockproject.dto.Account.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.dto.Course.CourseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemoveWishListResponse {
    private String message;
    private String error;
    private Integer code;
    private DataResponse data;
    private List<CourseDTO> courses;

    public RemoveWishListResponse(String message, String error, Integer code, DataResponse data) {
        this.message = message;
        this.error = error;
        this.code = code;
        this.data = data;
        this.courses = courses;
    }
@Getter
@Setter
public static class DataResponse {
    private List<CourseDTO> updatedWishlist;

    public DataResponse(List<CourseDTO> updatedWishlist) {
        this.updatedWishlist = updatedWishlist;
    }
}
}
