package com.ojt.mockproject.dto.ApiRequestLog.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojt.mockproject.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiRequestLogResponse {
    private Integer id;
    private boolean isGuest;
    private String ipAddress;
    private String apiEndpoint;
    private int requestCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Integer accountId;
}
