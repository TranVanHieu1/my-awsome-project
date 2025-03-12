package com.ojt.mockproject.dto.Account.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSubscribeRequestDTO {
    String userId;
    String instructorId;

}
