package com.ojt.mockproject.dto.Feedback;

import com.ojt.mockproject.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackRequestDTO {
//    private Integer id;
//    private LocalDateTime time;
    private String descrip;
//    private boolean isDelete;
    private Integer rating;
//    private Integer accountID;
    private Integer courseID;
}
