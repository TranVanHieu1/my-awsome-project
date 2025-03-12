package com.ojt.mockproject.dto.Chat.Responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class FindRoomResponse {
    private String roomId;
    private Integer idSender;
    private String avatarSender;
    private String nameSender;
    private String newMessage;
    private LocalDateTime createAt;
}
