package com.ojt.mockproject.dto.Account.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAvatarByAccountIdResponse {
    private Integer accountId;
    private String name;
    private String avatar;
}
