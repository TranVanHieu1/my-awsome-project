package com.ojt.mockproject.controller;

import com.ojt.mockproject.dto.Chat.Responses.ChatMessageResponse;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.ChatMessage;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageResponse chatMessageResponse) {
        if (chatMessageResponse == null) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        Integer senderId = chatMessageResponse.getSenderId();
        Integer recipientId = chatMessageResponse.getRecipientId();
        Optional<Account> sender = accountRepository.findById(senderId);
        Optional<Account> recipient = accountRepository.findById(recipientId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessageResponse.getId());
        chatMessage.setMessage(chatMessageResponse.getMessage());
        chatMessage.setSender(sender.get());
        chatMessage.setRecipient(recipient.get());
        chatMessage.setCreateAt(LocalDateTime.now());
        chatMessage.setRoomId(chatMessageResponse.getRoomId());
        chatMessageRepository.save(chatMessage);

        String destination = String.format("/topic/private/%s", chatMessageResponse.getRoomId());
        simpMessagingTemplate.convertAndSend(destination, chatMessageResponse);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(
            @Payload ChatMessageResponse chatMessageResponse,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        Integer senderId = chatMessageResponse.getSenderId();
        Integer recipientId = chatMessageResponse.getRecipientId();
        Optional<Account> sender = accountRepository.findById(senderId);
        Optional<Account> recipient = accountRepository.findById(recipientId);

        if (sender.isEmpty()) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        headerAccessor.getSessionAttributes().put("username", sender.get().getUsername());

        String destination = String.format("/topic/private/%s", chatMessageResponse.getRoomId());
        simpMessagingTemplate.convertAndSend(destination, chatMessageResponse);
    }

    @GetMapping("/chat/history/{roomId}")
    @ResponseBody
    public List<ChatMessageResponse> getChatHistory(@PathVariable String roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(roomId);
        return chatMessages.stream().map(chatMessage -> {
                ChatMessageResponse response = new ChatMessageResponse();
            response.setId(chatMessage.getId());
            response.setSenderId(chatMessage.getSender().getId());
            response.setRecipientId(chatMessage.getRecipient().getId());
            response.setMessage(chatMessage.getMessage());
            return response;
        }).collect(Collectors.toList());
    }
}
