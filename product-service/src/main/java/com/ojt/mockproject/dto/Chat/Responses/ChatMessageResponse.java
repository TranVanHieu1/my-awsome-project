package com.ojt.mockproject.dto.Chat.Responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ojt.mockproject.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ChatMessageResponse {
    private Integer id;
    private String roomId;
    private Integer senderId;
    private Integer recipientId;
    private String message;
    private String image;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
