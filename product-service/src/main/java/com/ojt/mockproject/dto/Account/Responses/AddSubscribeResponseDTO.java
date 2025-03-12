package com.ojt.mockproject.dto.Account.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddSubscribeResponseDTO {
    private int id;
    private String name;
    private String avatar;
}
