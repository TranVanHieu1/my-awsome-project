package com.ojt.mockproject.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.entity.Enum.OrderStatusEnum;
import com.ojt.mockproject.entity.Enum.PaymentMethodEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Orderr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "courses", nullable = false)
    private String courses;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @OneToMany(mappedBy = "orderr")
    private List<Transaction> transactions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum status;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Transient
    private String token;

    public List<Integer> getCoursesList() {
        if (courses == null || courses.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(courses.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public void setCoursesList(List<Integer> oldVersionList) {
        this.courses = oldVersionList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("courseId", courses);
            orderMap.put("account", account.getEmail());
            orderMap.put("orderId", id);

            return objectMapper.writeValueAsString(orderMap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
