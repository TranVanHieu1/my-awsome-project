package com.ojt.mockproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_request_log")
public class ApiRequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_guest", nullable = false)
    private boolean isGuest;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "api_endpoint", nullable = false)
    private String apiEndpoint;

    @Column(name = "request_count", nullable = false)
    private int requestCount;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

}
