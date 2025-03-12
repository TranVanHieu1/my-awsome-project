package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Dashboard.*;
import com.ojt.mockproject.entity.*;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.exceptionhandler.Dashboard.DashboardExceptionHandler;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.repository.*;
import com.ojt.mockproject.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DashboardService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AccountUtils accountUtils;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CrashCourseVideoRepository courseVideoRepository;

    public DashboardStudentResponse getDashboardStudent() {
        int countPurchasedCourse, countSubscription;
        Account curAcc = accountUtils.getCurrentAccount();
        if (curAcc.getRole() != AccountRoleEnum.STUDENT) {
            throw new DashboardExceptionHandler("You're not student, please login student account", ErrorCode.ACCOUNT_NOT_STUDENT);
        }

        //Total Purchased Course
        if (curAcc.getPurchasedCourse() != null) {
            countPurchasedCourse = curAcc.getPurchasedCourse().trim().split(",").length;
        } else {
            countPurchasedCourse = 0;
        }

        //Total Instructor Subscribing
        if (curAcc.getSubscribe() != null) {
            countSubscription = curAcc.getSubscribe().trim().split(",").length;
        } else {
            countSubscription = 0;
        }
        return new DashboardStudentResponse(countPurchasedCourse, countSubscription);
    }

    public DashboardInstructorResponse getDashboardInstructor() {
        Account curAcc = accountUtils.getCurrentAccount();
        if (curAcc.getRole() != AccountRoleEnum.INSTRUCTOR) {
            throw new DashboardExceptionHandler("You're not instructor, please login instructor account", ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
        }
        List<Course> courses = courseRepository.findByAccountId(curAcc.getId()).stream().filter(course ->
                course.getStatus().equals(CourseStatusEnum.APPROVED)||course.getStatus().equals(CourseStatusEnum.UNBLOCKED)
        ).collect(Collectors.toList());

        //Total Sales
        double totalSales = courses.stream().mapToDouble(course -> {
            if (course.getPurchasedStudents() != null && !course.getPurchasedStudents().isEmpty()) {
                return course.getPurchasedStudents().trim().split(",").length * course.getPrice().doubleValue();
            } else {
                return 0;
            }
        }).sum();

        //TotalCourse
        int totalCourses = courses.size();

        //Total Student Purchased Course
        int totalStudentPurchasedCourse = courses.stream().mapToInt(course -> {
            if (course.getPurchasedStudents() != null && !course.getPurchasedStudents().isEmpty()) {
                return course.getPurchasedStudents().trim().split(",").length;
            } else {
                return 0;
            }
        }).sum();

        //Total Subscription or Subscriber
        List<Account> accounts = accountRepository.findAll();
        String targetId = String.valueOf(curAcc.getId());
        long totalSubscriber = accounts.stream().filter(account -> {
            if (account.getSubscribe() != null && !account.getSubscribe().isEmpty()) {
                String[] subscript = account.getSubscribe().split(",");
                return Arrays.asList(subscript).contains(targetId);
            } else
                return false;
        }).count();

        //Purchased per Hour
        int totalPurchasedHour = countCoursesPurchasedBetweenPeriodTime(courses, LocalDateTime.now().minusHours(1), LocalDateTime.now());

        //View - Total View
        int totalView = courses.stream().mapToInt(Course::getView).sum();

        return new DashboardInstructorResponse(totalSales, totalCourses, totalStudentPurchasedCourse, totalSubscriber, totalView, totalPurchasedHour);
    }

    public WeeklySalesResponse weeklySales() {
        Account curAcc = accountUtils.getCurrentAccount();
        if (curAcc.getRole() != AccountRoleEnum.INSTRUCTOR) {
            throw new DashboardExceptionHandler("You're not instructor, please login instructor account", ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
        }
        List<Course> courses = courseRepository.findByAccountId(curAcc.getId()).stream().filter(course ->
                course.getStatus().equals(CourseStatusEnum.APPROVED)||course.getStatus().equals(CourseStatusEnum.UNBLOCKED)
        ).collect(Collectors.toList());
        //Total Sales in Week
        int totalSalesInWeek = countCoursesPurchasedBetweenPeriodTime(courses, LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
        int[] sales = new int[7];
        for (int i = 0; i < sales.length; i++) {
            LocalDateTime startDate = LocalDateTime.now().minusDays(i).with(LocalTime.MIN);
            LocalDateTime endDate = LocalDateTime.now().minusDays(i).with(LocalTime.MAX);
            sales[i] = countCoursesPurchasedBetweenPeriodTime(courses, startDate, endDate);
        }
        return new WeeklySalesResponse(totalSalesInWeek, sales[0], sales[1], sales[2], sales[3], sales[4], sales[5], sales[6]);
    }

    public SalesOfYearResponse salesOfYear() {
        Account curAcc = accountUtils.getCurrentAccount();
        if (curAcc.getRole() != AccountRoleEnum.INSTRUCTOR) {
            throw new DashboardExceptionHandler("You're not instructor, please login instructor account", ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
        }
        List<Course> courses = courseRepository.findByAccountId(curAcc.getId()).stream().filter(course ->
                course.getStatus().equals(CourseStatusEnum.APPROVED)||course.getStatus().equals(CourseStatusEnum.UNBLOCKED)
        ).collect(Collectors.toList());
        float sales[] = new float[13];
        for (int i = 0; i < LocalDateTime.now().getMonthValue(); i++)
            sales[LocalDateTime.now().getMonthValue() - i] = totalCoursesPurchasedBetweenPeriodTime(courses,
                    LocalDateTime.now().minusMonths(i).with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN),
                    LocalDateTime.now().minusMonths(i).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX));

        return new SalesOfYearResponse(sales[1], sales[2], sales[3], sales[4], sales[5], sales[6], sales[7], sales[8], sales[9],
                sales[10], sales[11], sales[12]);
    }


    public List<CoursesDashboardResponse> getCourseDashboardByInstructor() {
        List<CoursesDashboardResponse> result = new ArrayList<>();
        Account curAcc = accountUtils.getCurrentAccount();
        if (curAcc.getRole() != AccountRoleEnum.INSTRUCTOR) {
            throw new DashboardExceptionHandler("You're not instructor, please login instructor account", ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
        }
        List<Course> listCourse = courseRepository.findByAccountId(curAcc.getId()).stream().filter(course ->
             course.getStatus().equals(CourseStatusEnum.APPROVED)||course.getStatus().equals(CourseStatusEnum.UNBLOCKED)
        ).toList();
        for (Course course : listCourse) {
            Media media = courseVideoRepository.findByCourseId(course.getId());
            result.add(new CoursesDashboardResponse(
                    media.getThumbnail(),
                    course.getName(),
                    course.getPurchasedStudents().trim().split(",").length,
                    course.getView()));
        }
        return result;
    }

    public List<CoursePendingResponese> coursePendingByInstructor() {
        List<CoursePendingResponese> result = new ArrayList<>();
        Account curAcc = accountUtils.getCurrentAccount();
        if (curAcc.getRole() != AccountRoleEnum.INSTRUCTOR) {
            throw new DashboardExceptionHandler("You're not instructor, please login instructor account", ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
        }
        List<Course> listCourse = courseRepository.findByAccountId(curAcc.getId()).stream().filter(course ->
                course.getStatus().equals(CourseStatusEnum.PENDING)
        ).toList();
        for (Course course : listCourse) {
            result.add(new CoursePendingResponese(
                    course.getName(),
                    ChronoUnit.DAYS.between(course.getCreateAt(), LocalDateTime.now())));
        }
        return result;
    }

    private int countCoursesPurchasedBetweenPeriodTime(List<Course> courses, LocalDateTime start, LocalDateTime end) {
        int count = 0;
        for (Course course : courses) {
            List<Orderr> orders = orderRepository.findByCoursesContaining(course.getId().toString());
            for (Orderr orderr : orders) {
                List<Transaction> transactions = transactionRepository
                        .findByOrderrAndCreateAtBetween(orderr, start, end);
                count += transactions.size();
            }
        }
        return count;
    }
    
    private float totalCoursesPurchasedBetweenPeriodTime(List<Course> courses, LocalDateTime start, LocalDateTime end) {
        float totalPurchasedByPeriodTime = 0;
        for (Course course : courses) {
            List<Orderr> orders = orderRepository.findByCoursesContaining(course.getId().toString());
            for (Orderr orderr : orders) {
                List<Transaction> transactions = transactionRepository
                        .findByOrderrAndCreateAtBetween(orderr, start, end);
                for (Transaction transaction : transactions) {
                    totalPurchasedByPeriodTime += transaction.getTotalPrice().floatValue();
                }
            }
        }
        return totalPurchasedByPeriodTime;
    }
}
