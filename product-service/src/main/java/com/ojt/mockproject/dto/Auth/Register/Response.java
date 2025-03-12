package com.ojt.mockproject.dto.Auth.Register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    int code;
    String message;
    T data;
}
