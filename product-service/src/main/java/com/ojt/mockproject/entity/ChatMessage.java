package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roomId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "sender_id", nullable = true)
    private Account sender;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "recipient_id", nullable = true)
    private Account recipient;

    private String message;

    private String image;

    @Column(name = "create_at", nullable = true)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;
}
