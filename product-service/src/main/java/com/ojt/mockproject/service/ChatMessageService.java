package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Chat.Responses.ChatMessageResponse;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.ChatMessage;
import com.ojt.mockproject.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    @Autowired
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessageResponse> findByRoomId(String roomId) {
        List<ChatMessageResponse> chatMessageResponses = new ArrayList<>();
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(roomId);
        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
            chatMessageResponse.setId(chatMessage.getId());
            chatMessageResponse.setRoomId(chatMessage.getRoomId());
            chatMessageResponse.setMessage(chatMessage.getMessage());
            chatMessageResponse.setSenderId(chatMessage.getSender().getId());
            chatMessageResponse.setRecipientId(chatMessage.getRecipient().getId());
            chatMessageResponse.setCreateAt(chatMessage.getCreateAt());
            chatMessageResponse.setUpdateAt(chatMessage.getUpdateAt());
            chatMessageResponses.add(chatMessageResponse);
        }
        return chatMessageResponses;
    }

    public ChatMessageResponse saveMessage(ChatMessageResponse chatMessageResponse) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(chatMessageResponse.getRoomId());
        chatMessage.setMessage(chatMessageResponse.getMessage());
        // Assuming sender and recipient IDs are present
        Account sender = new Account();
        sender.setId(chatMessageResponse.getSenderId());
        Account recipient = new Account();
        recipient.setId(chatMessageResponse.getRecipientId());
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);
        chatMessage.setCreateAt(LocalDateTime.now());
        chatMessage.setUpdateAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        return chatMessageResponse;
    }

    public List<String> findRoomsByAccountId(Integer accountId) {
        return chatMessageRepository.findRoomIdsByAccountId(accountId);
    }

    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

}
