package com.ojt.mockproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.config.KafkaTopicConfig;
import com.ojt.mockproject.dto.Order.Requests.OrderRequestDTO;
import com.ojt.mockproject.dto.Order.Requests.OrderRequestUpdateDTO;
import com.ojt.mockproject.dto.Order.Requests.ReturnOrderPaymentDTO;
import com.ojt.mockproject.dto.Order.Responses.DailyEarningsResponse;
import com.ojt.mockproject.dto.Order.Responses.GenericResponse;
import com.ojt.mockproject.dto.Order.Responses.OrderResponseDTO;
import com.ojt.mockproject.dto.Transaction.TransactionRequestDTO;
import com.ojt.mockproject.dto.Transaction.TransactionResponseDTO;
import com.ojt.mockproject.dto.Wallet.Responses.BuyCourseResponse;
import com.ojt.mockproject.email.EmailService;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Enum.*;
import com.ojt.mockproject.entity.Orderr;
import com.ojt.mockproject.entity.Wallet;
import com.ojt.mockproject.exceptionhandler.*;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletAppException;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.order.FailedToPayException;
import com.ojt.mockproject.exceptionhandler.order.OrderException;
import com.ojt.mockproject.exceptionhandler.order.UnableToGetInformationFromExtractedOrder;
import com.ojt.mockproject.exceptionhandler.token.UnableToExtractTokenForReturnOrder;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.OrderRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.StringUtil;
import com.ojt.mockproject.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import static com.ojt.mockproject.entity.Enum.OrderStatusEnum.SUCCESS;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaTemplate<String, OrderResponseDTO> orderKafkaTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JWTService jwtService;


    @Autowired
    private StringUtil stringUtil;

    @Autowired
    @Lazy
    private WalletService walletService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CourseRepository courseRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WalletLogService walletLogService;

    public List<OrderResponseDTO> getAllOrders() {
        try {
            List<Orderr> orders = orderRepository.findAll();
            return convertToResponseDTOs(orders);
        } catch (Exception e) {
            throw new OrderException("Failed to retrieve orders", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public OrderResponseDTO getOrderById(Integer id) {
        try {
            Orderr order = orderRepository.findById(id)
                    .orElseThrow(() -> new OrderException("Order not found with id: " + id, ErrorCode.COURSE_NOT_FOUND));
            ValidationUtils.validateOrderIsDeleted(order);
            return convertToResponseDTO(order);
        } catch (OrderException e) {
            // Nếu exception là OrderException thì ném lại exception đó
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to retrieve order", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void hasPurchasedCoursesBefore(Integer accountId, List<Integer> courses) {
        Set<String> uniqueCourses = getAllUniqueCourses(accountId);
        for (Integer course : courses) {
            if (uniqueCourses.contains(course.toString().trim())) {

                throw new ValidationException("The course " + course + " has already been purchased.", ErrorCode.INVALID_INPUT);
            }
        }

    }


    @Transactional
    public GenericResponse<OrderResponseDTO> createOrder(OrderRequestDTO orderRequestDTO) {
        try {
            AccountUtils accountUtils;
            accountUtils = new AccountUtils();
            Account curAccount =accountUtils.getCurrentAccount();

            Account account = accountRepository.findById(curAccount.getId())
                    .orElseThrow(() -> new AccountException("Account not found with id: " + curAccount.getId(), ErrorCode.USER_NOT_FOUND));

            //Check Validation
            ValidationUtils.validateUniqueCourseIds(orderRequestDTO.getCourses());
            hasPurchasedCoursesBefore(account.getId(), orderRequestDTO.getCourses());

            Orderr order = new Orderr();
            order.setAccount(account);
            order.setStatus(OrderStatusEnum.PENDING);
            order.setCreateAt(LocalDateTime.now());
            order.setIsDeleted(false);

            if (orderRequestDTO.getCourses() != null && !orderRequestDTO.getCourses().isEmpty()) {
                order.setCoursesList(orderRequestDTO.getCourses());
            }
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Integer courseId : order.getCoursesList()) {
                Course course = courseService.getCourseById(courseId);
                totalPrice = totalPrice.add(course.getPrice());
            }
            order.setTotalPrice(totalPrice);

            // Process payment based on payment method
            PaymentMethodEnum paymentMethod = orderRequestDTO.getPaymentMethod();
            if (paymentMethod == PaymentMethodEnum.WALLET) {
                // CALL FUNC | Buy course by wallet & Create wallet log

                ResponseEntity<BuyCourseResponse> response = walletService.buyCourseByWallet(order.getAccount().getId(), order.getCourses(), order.getTotalPrice());
                // VALIDATION | Response message
                if (!response.getStatusCode().is2xxSuccessful()) {
                    BuyCourseResponse buyCourseResponse = response.getBody();
                    return new GenericResponse<>("Failed to create order", buyCourseResponse != null ? buyCourseResponse.getError() : "Unknown error", response.getStatusCodeValue(), null);
                }
                // SET VALUE | set enum WALLET & status SUCCESS
                order.setPaymentMethod(paymentMethod);
                order.setStatus(OrderStatusEnum.SUCCESS);

                Orderr savedOrder = orderRepository.save(order);

                // CALL FUNC | Add user to course and vice versa
                addUserToCourseAndViceVersa(account, orderRequestDTO.getCourses(), savedOrder);

                OrderResponseDTO responseDTO = convertToResponseDTO(savedOrder);

                // SEND KAFKA | Send message, service email handle
                orderKafkaTemplate.send("buy-courses-success-by-wallet", convertToResponseDTO(order));

                return new GenericResponse<>("Order created successfully", null, 201, responseDTO);

            } else if (paymentMethod == PaymentMethodEnum.PAYOS) {
                // Handle payment with QR
                order.setPaymentMethod(paymentMethod);
                orderKafkaTemplate.send("create-payment", convertToResponseDTO(order));
            } else {
                throw new OrderException("Invalid payment method", ErrorCode.INVALID_INPUT);
            }

            return new GenericResponse<>("Order created successfully", null, 201, convertToResponseDTO(order));
        } catch (ValidationException | AccountException | OrderException | CourseException e) {
            return new GenericResponse<>("Failed to create order", e.getMessage(), ErrorCode.INVALID_INPUT.getCode(), null);
        } catch (Exception e) {
            return new GenericResponse<>("Failed to create order", e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR.getCode(), null);
        }
    }

    public static TransactionRequestDTO getTransactionRequestDTO(Account account, Orderr savedOrder) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAccountId(account.getId());
        transactionRequestDTO.setOrderId(savedOrder.getId());
        transactionRequestDTO.setTotalPrice(savedOrder.getTotalPrice());
        transactionRequestDTO.setPaymentMethod(savedOrder.getPaymentMethod());
        transactionRequestDTO.setStatus(TransactionStatusEnum.SUCCESS);
        return transactionRequestDTO;
    }


    @Transactional
    public OrderResponseDTO updateOrder(Integer id, OrderRequestUpdateDTO orderRequestDTO) {
        try {

            AccountUtils accountUtils = new AccountUtils();
            Account curAccount =accountUtils.getCurrentAccount();

            Account account = accountRepository.findById(curAccount.getId())
                    .orElseThrow(() -> new AccountException("Account not found with id: " + curAccount.getId(), ErrorCode.USER_NOT_FOUND));


            ValidationUtils.validateUniqueCourseIds(orderRequestDTO.getCourses());
            hasPurchasedCoursesBefore(account.getId(), orderRequestDTO.getCourses());

            Orderr order = orderRepository.findById(id)
                    .orElseThrow(() -> new OrderException("Order not found with id: " + id, ErrorCode.ORDER_NOT_FOUND));

            order.setAccount(account);
            order.setStatus(orderRequestDTO.getStatus());
            order.setIsDeleted(false);

            if (orderRequestDTO.getCourses() != null && !orderRequestDTO.getCourses().isEmpty()) {
                order.setCoursesList(orderRequestDTO.getCourses());
            }

            Orderr updatedOrder = orderRepository.save(order);

            return convertToResponseDTO(updatedOrder);
        } catch (OrderException | AccountException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to update order", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public void deleteOrder(Integer id) {
        try {
            checkIfExistsById(id);
            orderRepository.deleteById(id);
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to delete order", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void changeIsDeleteOrder(Integer orderId, boolean is_delete) {
        try {
            Orderr order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderException("Order not found with id: " + orderId, ErrorCode.ORDER_NOT_FOUND));
            order.setIsDeleted(is_delete);
            orderRepository.save(order);
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to delete order", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    public List<OrderResponseDTO> getOrdersByStatus(OrderStatusEnum status) {
        try {
            List<Orderr> list = orderRepository.findByStatusAndIsDeleted(status,false);
            return convertToResponseDTOs(list);
        } catch (Exception e) {
            throw new AccountException("Failed to retrieve order by status", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<OrderResponseDTO> getOrdersByAccountId(Integer accountId) {
        try {
            List<Orderr> list = orderRepository.findByAccountIdAndIsDeleted(accountId,false);
            return convertToResponseDTOs(list);
        } catch (Exception e) {
            throw new AccountException("Failed to retrieve order by AccountId", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<OrderResponseDTO> getOrdersByAccountIdAndStatus(Integer accountId, OrderStatusEnum status) {
        try {
            List<Orderr> list = orderRepository.findByAccountIdAndStatusAndIsDeleted(accountId, status,false);
            return convertToResponseDTOs(list);
        } catch (Exception e) {
            throw new AccountException("Failed to retrieve order by AccountId", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    public Set<String> getAllUniqueCourses(Integer accountId) {
        try {
            List<OrderResponseDTO> orders = getOrdersByAccountIdAndStatus(accountId, SUCCESS);
            Set<String> uniqueCourses = new HashSet<>();

            for (OrderResponseDTO order : orders) {
                if (order.getCourses() != null && !order.getCourses().isEmpty()) {
                    String[] courses = order.getCourses().split(",");
                    for (String course : courses) {
                        uniqueCourses.add(course.trim());
                    }
                }
            }
            return uniqueCourses;
        } catch (Exception e) {
            throw new CourseAppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    public BigDecimal getTotalOrdersByAccount(Integer accountId) {
        try {
            List<OrderResponseDTO> orders = getOrdersByAccountIdAndStatus(accountId, SUCCESS);
            BigDecimal total = BigDecimal.ZERO;

            for (OrderResponseDTO order : orders) {
                total = total.add(order.getTotalPrice());

            }
            return total;
        } catch (Exception e) {
            throw new CourseAppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    private void checkIfExistsById(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderException("Order not found with id: " + id, ErrorCode.ORDER_NOT_FOUND);
        }
    }

    private List<OrderResponseDTO> convertToResponseDTOs(List<Orderr> orders) {
        List<OrderResponseDTO> returnList = new ArrayList<>();
        for (Orderr order : orders) {
            OrderResponseDTO responseDTO = new OrderResponseDTO();
            responseDTO.setId(order.getId());
            responseDTO.setTotalPrice(order.getTotalPrice());
            responseDTO.setStatus(order.getStatus());
            responseDTO.setCreateAt(order.getCreateAt());

            // Convert courses list to comma-separated string
            responseDTO.setCourses(order.getCoursesList().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));

            returnList.add(responseDTO);
        }
        return returnList;
    }


    private OrderResponseDTO convertToResponseDTO(Orderr order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setTotalPrice(order.getTotalPrice());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setCreateAt(order.getCreateAt());
       responseDTO.setPaymentMethod(order.getPaymentMethod());
        // Convert courses list to comma-separated string
        responseDTO.setCourses(order.getCoursesList().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

        return responseDTO;
    }


    //1. Check if the order is cancel
    //2. If the order is success, save order to db
    //3. Add Student to Course
    //4. Add Course to Student
    public void handleReturnOrder(String token, boolean cancel, String status) throws Exception {

        //Account in token
        Account account;

        //Order in token
        Orderr orderr = new Orderr();

        //Claims from token
        new ReturnOrderPaymentDTO();
        ReturnOrderPaymentDTO returnOrderPaymentDTO;

        //Courses List from token
        List<Integer> coursesList;

        if (!cancel && status.equals("PAID")) {
            try {

                //Extract token to get string
                String tokenString = jwtService.extractSubject(token);

                //Turn tokenString into ReturnOrderPaymentDTO
                returnOrderPaymentDTO = objectMapper.readValue(tokenString, ReturnOrderPaymentDTO.class);

            } catch (Exception e) {
                throw new UnableToExtractTokenForReturnOrder("The token sent from payment service is unable to read! \n Please re-check at instance OrderService.handleReturnOrder");
            }

            try {
                //Get account
                account = accountService.getAccountByEmail(returnOrderPaymentDTO.getAccount());

                //Get all courseId
                coursesList = stringUtil.stringToList(returnOrderPaymentDTO.getCourseId());

                //Get Order
                orderr = orderRepository.findById(returnOrderPaymentDTO.getOrderId()).orElseThrow(() -> new OrderException("Failed to retrieve orders", ErrorCode.INTERNAL_SERVER_ERROR));
            } catch (Exception e) {
                throw new UnableToGetInformationFromExtractedOrder("The information is unable to be retrieve! \n Please re-check at instance OrderService.handleReturnOrder");
            }

            //Save order to database
            orderRepository.save(orderr);

            //Add user into a course and vice versa
            //With each ID equals 1 Course
            for (Integer courseId : coursesList) {
                //Lấy khóa học đó ra
                Course course = courseService.getCourseById(courseId);

                // * Handle add student to course * //

                //Retrieve the purchased student list from the course
                List<Integer> listStudent = stringUtil.stringToList(course.getPurchasedStudents());
                //Add the student into the student list above
                if (!listStudent.contains(account.getId())) {
                    listStudent.add(account.getId());
                }

                //save the student list back to the course
                course.setPurchasedStudents(stringUtil.listToString(listStudent));
                //save to db
                courseService.saveCourse(course);

                // * Handle add course to student * //

                //Get all the purchased course of the student
                List<Integer> purchasedCourseList = stringUtil.stringToList(account.getPurchasedCourse());
                if (!purchasedCourseList.contains(course.getId())) {
                    purchasedCourseList.add(course.getId());
                }

                //save the course list back to the student
                account.setPurchasedCourse(stringUtil.listToString(purchasedCourseList));
                //save to db
                accountService.saveAccountChanges(account);

                orderr.setStatus(SUCCESS);
                orderRepository.save(orderr);
            }
        } else if (cancel && status.equals("CANCELLED")) {
            orderr.setStatus(OrderStatusEnum.FAILED);
            orderRepository.save(orderr);
            throw new FailedToPayException("You have failed to pay for the order");
        }
    }

    public void addUserToCourseAndViceVersa(Account account, List<Integer> courseIds, Orderr savedOrder) throws Exception {
        for (Integer courseId : courseIds) {
            // CALL FUNC | Get the course
            Course course = courseService.getCourseById(courseId);

            // CALL FUNC | Handle add balance for intructor wallet
            Integer instructorId = course.getAccount().getId();
            walletService.addPriceToWallet(instructorId, course.getPrice());

            // HANDLE | Handle adding the student to the course
            // * if null or empty
            String purchasedStudents = course.getPurchasedStudents();
            if (purchasedStudents == null || purchasedStudents.isEmpty()) {
                course.setPurchasedStudents("" + account.getId());
            } else {
                course.setPurchasedStudents(purchasedStudents + "," + account.getId());
            }
            // HANDLE | Handle adding the course to student
            String purchasedCourses = account.getPurchasedCourse();
            if (purchasedCourses == null || purchasedCourses.isEmpty()) {
                account.setPurchasedCourse("" + course.getId());
            } else {
                account.setPurchasedCourse(purchasedCourses + "," + course.getId());
            }
            // SAVE DB | course & account
            courseService.saveCourse(course);
            accountService.saveAccountChanges(account);

            // CREATE TRANSACTION

            TransactionRequestDTO transactionStudentRequestDTO = getTransactionRequestDTO(account, savedOrder);
            transactionStudentRequestDTO.setTransactionTypeEnum(TransactionTypeEnum.SUBTRACT);
            TransactionResponseDTO transactionStudentResponseDTO = transactionService.createTransaction(transactionStudentRequestDTO);

            TransactionRequestDTO transactionInstructorRequestDTO = getTransactionRequestDTO(course.getAccount(), savedOrder);
            transactionInstructorRequestDTO.setTransactionTypeEnum(TransactionTypeEnum.ORDER);
            TransactionResponseDTO transactionInstructorResponseDTO = transactionService.createTransaction(transactionInstructorRequestDTO);

            Wallet walletStudent = walletService.getWalletByAccountId(account.getId());
            Wallet walletInstructor = walletService.getWalletByAccountId(instructorId);
                if (walletStudent.getId() == null) {
                    throw new WalletAppException(ErrorCode.WALLET_NOT_FOUND);
                }
                walletService.createWalletLog(walletStudent, course.getPrice(), transactionStudentResponseDTO.getId(), WalletLogTypeEnum.SUBTRACT);
                walletService.createWalletLog(walletInstructor, course.getPrice(), transactionInstructorResponseDTO.getId(), WalletLogTypeEnum.ADD);
        }
    }

    public List<DailyEarningsResponse> getDailyEarningsByAccountId() {
        try {
            AccountUtils accountUtils = new AccountUtils();
            Account curAccount = accountUtils.getCurrentAccount();
            Account account = accountRepository.findById(curAccount.getId())
                    .orElseThrow(() -> new AccountException("Account not found with id: " + curAccount.getId(), ErrorCode.USER_NOT_FOUND));
            if (account.getRole() != AccountRoleEnum.INSTRUCTOR) {
                throw new AccountException("Account is not a instructor: " + account.getId(), ErrorCode.ACCOUNT_NOT_STUDENT);
            }
            ValidationUtils.validateAccountIsDeleted(account);
            LocalDateTime startDate = LocalDateTime.now().minusDays(11).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            List<Orderr> orders = orderRepository.findAllByStatusAndCreateAtBetweenAndIsDeleted(OrderStatusEnum.SUCCESS, startDate, endDate,false);
            Map<LocalDate, DailyEarningsResponse> dailyEarningsMap = new TreeMap<>();

            for (Orderr order : orders) {
                List<Integer> courseIds = order.getCoursesList();
                if (courseIds == null || courseIds.isEmpty()) {
                    throw new CourseException("No courses found for order id: " + order.getId(), ErrorCode.COURSE_NOT_FOUND);
                }

                BigDecimal totalAmount = BigDecimal.ZERO;
                int totalSaleInDay = 0;

                for (Integer courseId : courseIds) {
                    Optional<Course> optionalCourse = courseRepository.findByIdAndIsDeleted(courseId,false);
                    if (optionalCourse.isPresent()) {
                        Course course = optionalCourse.get();
                        if (course.getAccount().getId().equals(account.getId())) {
                            totalAmount = totalAmount.add(course.getPrice());
                            totalSaleInDay++;
                        }
                    }
                }

                LocalDate date = order.getCreateAt().toLocalDate();
                DailyEarningsResponse dailyEarnings = dailyEarningsMap.getOrDefault(date, new DailyEarningsResponse(date, BigDecimal.ZERO, 0));
                dailyEarnings.setTotalAmount(dailyEarnings.getTotalAmount().add(totalAmount));
                dailyEarnings.setTotalSaleInDay(dailyEarnings.getTotalSaleInDay() + totalSaleInDay);
                dailyEarningsMap.put(date, dailyEarnings);
            }

            return new ArrayList<>(dailyEarningsMap.values());
        } catch (AccountException | CourseException e) {
            throw e;
        } catch (Exception e) {
           // System.out.println(e.getMessage());
            throw new OrderException(e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


}

