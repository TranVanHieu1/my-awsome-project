package com.ojt.mockproject.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.dto.Account.Responses.SubscriptionResponseDTO;
import com.ojt.mockproject.dto.Course.CourseSubscriptionDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public SubscriptionResponseDTO getUserSubscriptions(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //purchasedCourses la JSONString. Vi du: {"course_id": 1, "purchase_date": "2023-05-01"}
        List<Map<String, String>> purchasedCourses;
        try {
            purchasedCourses = objectMapper.readValue(account.getPurchasedCourse(), new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing purchased courses JSON", e);
        }

        SubscriptionResponseDTO responseDTO = new SubscriptionResponseDTO();
        List<CourseSubscriptionDTO> subscriptions = new ArrayList<>();

        for (Map<String, String> purchasedCourse : purchasedCourses) {
            Integer courseId = Integer.valueOf(purchasedCourse.get("course_id"));
            String purchaseDate = purchasedCourse.get("purchase_date");

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // co the chi lay ten thoi cung duoc. neu muon(Lay courseID, CourseName, Ngay thanh toan va Ten giang vien)
            CourseSubscriptionDTO subscriptionDTO = new CourseSubscriptionDTO();
            subscriptionDTO.setCourseId(courseId);
            subscriptionDTO.setCourseName(course.getName());
            subscriptionDTO.setPurchaseDate(purchaseDate);
            subscriptionDTO.setInstructorName(course.getAccount().getName()); // Set instructor's name

            subscriptions.add(subscriptionDTO);
        }

        responseDTO.setSubscriptions(subscriptions);
        return responseDTO;
    }
}
