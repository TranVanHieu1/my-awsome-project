package com.ojt.mockproject.dto.Auth.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadAvatarResponse {
    private Integer accountId;
    private String imageUrl;
}
