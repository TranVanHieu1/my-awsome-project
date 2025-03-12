package com.ojt.mockproject.dto.Account.Responses;
import com.ojt.mockproject.dto.Course.CourseSubscriptionDTO;
import lombok.*;
import java.util.List;
@Setter
@Getter
public class SubscriptionResponseDTO {
    private List<CourseSubscriptionDTO> subscriptions;
}
