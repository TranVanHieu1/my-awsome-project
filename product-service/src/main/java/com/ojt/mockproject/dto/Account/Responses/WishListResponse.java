package com.ojt.mockproject.dto.Account.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.dto.Course.CourseDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishListResponse {
    private String message;
    private String error;
    private Integer code;
    private List<CourseDTO> courses;

    public WishListResponse(String message, String error, Integer code, List<CourseDTO> courses) {
        this.message = message;
        this.error = error;
        this.code = code;
        this.courses = courses;
    }
}
