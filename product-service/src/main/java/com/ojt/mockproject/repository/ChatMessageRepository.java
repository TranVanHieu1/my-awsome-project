package com.ojt.mockproject.repository;

import com.ojt.mockproject.dto.Chat.Responses.ChatMessageResponse;
import com.ojt.mockproject.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByRoomId(String roomId);

    @Query("SELECT DISTINCT cm.roomId FROM ChatMessage cm WHERE cm.sender.id = :accountId OR cm.recipient.id = :accountId")
    List<String> findRoomIdsByAccountId(Integer accountId);

    ChatMessage findTopByRoomIdOrderByCreateAtDesc(String roomId);
}
