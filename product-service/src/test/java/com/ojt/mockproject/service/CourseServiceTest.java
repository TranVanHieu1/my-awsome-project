package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Course.*;
import com.ojt.mockproject.entity.*;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.entity.certificate_quiz.Certificate;
import com.ojt.mockproject.entity.certificate_quiz.Quiz;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.course.UnableToSaveCourseException;
import com.ojt.mockproject.repository.*;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.StringUtil;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AccountUtils accountUtils;

    @Mock
    private CourseChapterRepository courseChapterRepository;

    @Mock
    private CrashCourseVideoRepository crashCourseVideoRepository;

    @Mock
    private CourseChapterService courseChapterService;

    @Mock
    private SmallCourseVideoRepository smallCourseVideoRepository;

    @Mock
    private SmallCourseVideoService smallCourseVideoService;

    @Mock
    private CrashCourseVideoService crashCourseVideoService;

    @Mock
    private FeedBackService feedBackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private CertificateService certificateService;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private QuizService quizService;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private StringUtil stringUtil;

    @Mock
    private UploadFileUtils uploadFileUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCourseById() {
        Course course = new Course();
        course.setId(1);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1);

        assertEquals(course, result);
        verify(courseRepository, times(1)).findById(1);
    }

    @Test
    public void testViewCourseById() throws IOException {
        // Arrange
        Integer courseId = 1;

        Account account = new Account();
        account.setName("Instructor Name");

        Course course = new Course();
        course.setId(courseId);
        course.setName("Test Course");
        course.setCreateAt(LocalDateTime.now());
        course.setDescription("Description");
        course.setView(10);
        course.setAccount(account);
        course.setCategory("Category");
        course.setPrice(BigDecimal.valueOf(100));
        course.setShortDescription("Short Description");
        course.setRequirements("Requirements");
        course.setFeedbacks(new ArrayList<>());  // Initialize the feedback list
        course.setChapters(new ArrayList<>());   // Initialize the chapters list
        course.setStudentWillLearn("Student will learn");
        course.setAudioLanguage("English");
        course.setIsDeleted(false);

        Feedback feedback = new Feedback();
        feedback.setCourse(course);
        feedback.setRating(5);
        course.getFeedbacks().add(feedback);

        Lecture lecture = new Lecture();
        lecture.setIsDeleted(false);
        lecture.setDuration(60);

        CourseChapter courseChapter = new CourseChapter();
        courseChapter.setCourse(course);
        courseChapter.setSmallCourseVideos(new ArrayList<>()); // Initialize the lectures list
        courseChapter.getSmallCourseVideos().add(lecture);
        course.getChapters().add(courseChapter);

        Media media = new Media();
        media.setThumbnail("thumbnailUrl");
        course.setCrashCourseVideos(Collections.singletonList(media));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(uploadFileUtils.getSignedImageUrl("thumbnailUrl")).thenReturn("signedImageUrl");
        when(feedBackService.formatDate(any(LocalDateTime.class))).thenReturn("formattedDate");

        // Act
        CourseDTO result = courseService.viewCourseById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("Test Course", result.getName());
        assertEquals("Instructor Name", result.getInstructorName());
        assertEquals("Category", result.getCategory());
        assertEquals("Description", result.getDescription());
        assertEquals("Short Description", result.getShortDescription());
        assertEquals("Requirements", result.getRequirement());
        assertEquals(BigDecimal.valueOf(100), result.getPrice());
        assertEquals(5.0, result.getRating(), 0.01); // Use a delta for floating-point comparison
        assertEquals("formattedDate", result.getCreateAt());
        assertEquals(60, result.getDuration());
        assertEquals(11, result.getView()); // The view count should be incremented by 1
        assertEquals("signedImageUrl", result.getImgUrl());
        assertEquals("Student will learn", result.getStudentWillLearn());
        assertEquals("English", result.getAudioLanguage());

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(course);
        verify(uploadFileUtils, times(1)).getSignedImageUrl("thumbnailUrl");
        verify(feedBackService, times(1)).formatDate(any(LocalDateTime.class));
    }


    @Test
    public void testGetCourseByName() throws IOException {
        // Arrange
        String courseName = "Test Course";

        Account account = new Account();
        account.setName("Instructor Name");

        Course course = new Course();
        course.setId(1);
        course.setName(courseName);
        course.setIsDeleted(false);
        course.setCreateAt(LocalDateTime.now());
        course.setAccount(account);
        course.setCategory("Category");
        course.setPrice(BigDecimal.valueOf(100));
        course.setDescription("Description");
        course.setView(10);
        course.setStatus(CourseStatusEnum.APPROVED);

        Media media = new Media();
        media.setThumbnail("thumbnailUrl");

        course.setCrashCourseVideos(Arrays.asList(media));
        course.setFeedbacks(new ArrayList<>());  // Initialize the feedback list
        course.setChapters(new ArrayList<>());   // Initialize the chapters list

        Feedback feedback = new Feedback();
        feedback.setCourse(course);
        feedback.setRating(5);
        feedback.setIsDeleted(false);
        course.getFeedbacks().add(feedback);

        Lecture lecture = new Lecture();
        lecture.setIsDeleted(false);
        lecture.setDuration(120);

        CourseChapter courseChapter = new CourseChapter();
        courseChapter.setCourse(course);
        courseChapter.setSmallCourseVideos(new ArrayList<>()); // Initialize the lectures list
        courseChapter.getSmallCourseVideos().add(lecture);
        course.getChapters().add(courseChapter);

        when(courseRepository.findByNameContainingIgnoreCase(courseName)).thenReturn(Collections.singletonList(course));
        when(uploadFileUtils.getSignedImageUrl("thumbnailUrl")).thenReturn("signedImageUrl");

        // Act
        List<CourseDTO> result = courseService.getCourseByName(courseName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CourseDTO courseDTO = result.get(0);
        assertEquals(course.getId(), courseDTO.getId());
        assertEquals(courseName, courseDTO.getName());
        assertEquals(course.getAccount().getName(), courseDTO.getInstructorName());
        assertEquals(course.getCategory(), courseDTO.getCategory());
        assertEquals(course.getPrice(), courseDTO.getPrice());
        assertEquals(5.0, courseDTO.getRating(), 0.01); // Use a delta for floating-point comparison
        assertEquals(course.getDescription(), courseDTO.getDescription());
        assertEquals("signedImageUrl", courseDTO.getImgUrl());
        assertEquals(120, courseDTO.getDuration());
        assertEquals(10, courseDTO.getView());

        verify(courseRepository, times(1)).findByNameContainingIgnoreCase(courseName);
        verify(uploadFileUtils, times(1)).getSignedImageUrl("thumbnailUrl");
    }


    @Test
    public void testGetCourseByName_NoCoursesFound() {
        // Arrange
        String courseName = "NonExistentCourse";
        List<Course> courses = new ArrayList<>();

        when(courseRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(courses);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.getCourseByName(courseName);
        });
        assertEquals("No course", exception.getMessage());
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void testGetCourseById_NotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class, () -> courseService.getCourseById(1));

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        verify(courseRepository, times(1)).findById(1);
    }

    @Test
    public void testSaveCourse() {
        Course course = new Course();
        courseService.saveCourse(course);

        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testSaveCourse_Exception() {
        Course course = new Course();
        doThrow(new RuntimeException()).when(courseRepository).save(any(Course.class));

        UnableToSaveCourseException exception = assertThrows(UnableToSaveCourseException.class, () -> courseService.saveCourse(course));

        assertEquals("The course is unable to save to database, please re-check it", exception.getMessage());
        verify(courseRepository, times(1)).save(course);
    }


    @Test
    public void testGetCourseByAccount_Success() {
        // Arrange
        Account account = new Account();
        account.setId(1);

        Course course = new Course();
        course.setId(1);
        course.setName("Course 1");
        course.setCategory("Category");
        course.setCreateAt(LocalDateTime.now());
        course.setIsDeleted(false);
        course.setPurchasedStudents("[1,2,3]"); // Mocking as a JSON string
        course.setChapters(Collections.singletonList(new CourseChapter())); // Mocking chapters

        when(accountUtils.getCurrentAccount()).thenReturn(account);
        when(courseRepository.findCourseByAccount(account)).thenReturn(Collections.singletonList(course));
        when(feedBackService.formatDate(course.getCreateAt())).thenReturn("01/01/2024");
        when(stringUtil.stringToList(course.getPurchasedStudents())).thenReturn(Arrays.asList(1, 2, 3));

        // Act
        List<FindCourseResponseDTO> result = courseService.getCourseByAccount();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        FindCourseResponseDTO dto = result.get(0);
        assertEquals(course.getId(), dto.getId());
        assertEquals(3, dto.getSales());
        assertEquals(1, dto.getParts());
        assertEquals("Category", dto.getCategory());
        assertEquals(course.getStatus(), dto.getStatus());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(courseRepository, times(1)).findCourseByAccount(account);
        verify(feedBackService, times(1)).formatDate(course.getCreateAt());
        verify(stringUtil, times(1)).stringToList(course.getPurchasedStudents());
    }

    @Test
    public void testGetCourseByAccount_NotLoggedIn() {
        // Arrange
        when(accountUtils.getCurrentAccount()).thenThrow(new NotLoginException("Not Login"));

        // Act & Assert
        NotLoginException exception = assertThrows(NotLoginException.class, () -> {
            courseService.getCourseByAccount();
        });

        assertEquals("Not Login", exception.getMessage());

        verify(accountUtils, times(1)).getCurrentAccount();
        verify(courseRepository, times(0)).findCourseByAccount(any());
        verify(feedBackService, times(0)).formatDate(any());
        verify(stringUtil, times(0)).stringToList(any());
    }

    @Test
    public void testGetAllCourses() throws IOException {
        // Arrange
        Account account = new Account();
        account.setName("Instructor Name");

        Course course = new Course();
        course.setId(1);
        course.setName("Test Course");
        course.setIsDeleted(false);
        course.setCreateAt(LocalDateTime.now());
        course.setAccount(account);
        course.setCategory("Category");
        course.setPrice(BigDecimal.valueOf(100));
        course.setDescription("Description");
        course.setShortDescription("Short Description");
        course.setRequirements("Requirements");
        course.setView(10);
        course.setStatus(CourseStatusEnum.APPROVED);
        course.setFeedbacks(new ArrayList<>());  // Initialize the feedback list
        course.setChapters(new ArrayList<>());   // Initialize the chapters list
        course.setStudentWillLearn("Student will learn");
        course.setAudioLanguage("English");

        Feedback feedback = new Feedback();
        feedback.setCourse(course);
        feedback.setRating(5);
        course.getFeedbacks().add(feedback);

        Lecture lecture = new Lecture();
        lecture.setIsDeleted(false);
        lecture.setDuration(120);

        CourseChapter courseChapter = new CourseChapter();
        courseChapter.setCourse(course);
        courseChapter.setSmallCourseVideos(new ArrayList<>()); // Initialize the lectures list
        courseChapter.getSmallCourseVideos().add(lecture);
        course.getChapters().add(courseChapter);

        Media media = new Media();
        media.setThumbnail("thumbnailUrl");
        course.setCrashCourseVideos(Collections.singletonList(media));

        when(courseRepository.findAll()).thenReturn(Collections.singletonList(course));
        when(uploadFileUtils.getSignedImageUrl("thumbnailUrl")).thenReturn("signedImageUrl");
        when(feedBackService.formatDate(any(LocalDateTime.class))).thenReturn("formattedDate");

        // Act
        List<CourseDTO> result = courseService.getAllCourses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CourseDTO courseDTO = result.get(0);
        assertEquals(course.getId(), courseDTO.getId());
        assertEquals("Test Course", courseDTO.getName());
        assertEquals("Instructor Name", courseDTO.getInstructorName());
        assertEquals("Category", courseDTO.getCategory());
        assertEquals("Short Description", courseDTO.getShortDescription());
        assertEquals("Requirements", courseDTO.getRequirement());
        assertEquals(BigDecimal.valueOf(100), courseDTO.getPrice());
        assertEquals(5.0, courseDTO.getRating(), 0.01); // Use a delta for floating-point comparison
        assertEquals("formattedDate", courseDTO.getCreateAt());
        assertEquals(120, courseDTO.getDuration());
        assertEquals("Description", courseDTO.getDescription());
        assertEquals("signedImageUrl", courseDTO.getImgUrl());
        assertEquals(10, courseDTO.getView());
        assertEquals("Student will learn", courseDTO.getStudentWillLearn());
        assertEquals("English", courseDTO.getAudioLanguage());

        verify(courseRepository, times(1)).findAll();
        verify(uploadFileUtils, times(1)).getSignedImageUrl("thumbnailUrl");
        verify(feedBackService, times(1)).formatDate(any(LocalDateTime.class));
    }


//    @Test
//    public void testCreateCourse() {
//        // Arrange
//        CourseRequestDTO courseRequestDTO = new CourseRequestDTO();
//        courseRequestDTO.setBasicCourseTittle("New Course");
//        courseRequestDTO.setPrice(BigDecimal.valueOf(100));
//        courseRequestDTO.setBasicCourseCategory("Category");
//        courseRequestDTO.setBasicCourseDescription("Description");
//
//        Account account = new Account();
//        account.setName("User");
//        account.setRole(AccountRoleEnum.INSTRUCTOR);
//
//        when(accountUtils.getCurrentAccount()).thenReturn(account);
//        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
//            Course savedCourse = invocation.getArgument(0);
//            savedCourse.setId(1); // Mock setting ID after saving
//            savedCourse.setCreateAt(LocalDateTime.now());
//            return savedCourse;
//        });
//
//        // Act
//        CreateCourseResponseDTO result = courseService.createCourse(courseRequestDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("New Course", result.getBasicCourseTittle());
//        assertEquals("Category", result.getBasicCourseCategory());
//        assertEquals("Description", result.getBasicCourseDescription());
//        assertEquals(BigDecimal.valueOf(100), result.getPrice());
//        assertEquals("PENDING", result.getCourseStatus().toString());
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//        String formattedCreateAt = result.getCreateAt().format(String.valueOf(formatter));
//        assertNotNull(formattedCreateAt);
//
//        verify(courseRepository, times(1)).save(any(Course.class));
//        verify(certificateRepository, times(1)).save(any(Certificate.class));
//    }

    @Test
    public void testCreateCourse_NotLoggedIn() {
        CourseRequestDTO courseRequestDTO = new CourseRequestDTO();
        courseRequestDTO.setBasicCourseTittle("New Course");
        courseRequestDTO.setPrice(BigDecimal.valueOf(100));

        when(accountUtils.getCurrentAccount()).thenThrow(new NotLoginException("Not Login"));

        NotLoginException exception = assertThrows(NotLoginException.class, () -> courseService.createCourse(courseRequestDTO));

        assertEquals("Not Login", exception.getMessage());
    }

    @Test
    public void testCreateCourse_InvalidPrice() {
        CourseRequestDTO courseRequestDTO = new CourseRequestDTO();
        courseRequestDTO.setBasicCourseTittle("New Course");
        courseRequestDTO.setPrice(BigDecimal.valueOf(-100));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(courseRequestDTO));

        assertEquals("Price cannot be a negative number", exception.getMessage());
    }



    @Test
    public void testUpdateCourse_Success() {
        // Arrange
        Integer courseId = 1;
        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO();
        updateCourseDTO.setCourseTittle("Updated Course");
        updateCourseDTO.setCourseCategory("Updated Category");

        LocalDateTime now = LocalDateTime.now();

        Course oldCourse = new Course();
        oldCourse.setId(courseId);
        oldCourse.setName("Old Course");
        oldCourse.setCategory("Old Category");
        oldCourse.setIsDeleted(false);
        oldCourse.setIsOld(false);
        oldCourse.setVersion(1);
        oldCourse.setOldVersionList(new ArrayList<>());
        oldCourse.setCreateAt(now);

        Course newCourse = new Course();
        newCourse.setId(2);
        newCourse.setName("New Course");
        newCourse.setCreateAt(now);

        CourseChapter oldChapter = new CourseChapter();
        oldChapter.setId(1);
        oldChapter.setName("Old Chapter");
        oldChapter.setCourse(oldCourse);

        CourseChapter newChapter = new CourseChapter();
        newChapter.setId(2);
        newChapter.setName("New Chapter");

        Report oldReport = new Report();
        oldReport.setId(1);
        oldReport.setCourse(oldCourse);

        Report newReport = new Report();
        newReport.setId(2);
        newReport.setCourse(newCourse);

        Certificate oldCertificate = new Certificate();
        oldCertificate.setId(1);
        oldCertificate.setName("Old Certificate");

        Certificate newCertificate = new Certificate();
        newCertificate.setId(2);
        newCertificate.setName("New Certificate");

        Quiz oldQuiz = new Quiz();
        oldQuiz.setId(1);
        oldQuiz.setTitle("Old Quiz");

        Quiz newQuiz = new Quiz();
        newQuiz.setId(2);
        newQuiz.setTitle("New Quiz");

        // Mock behavior for courseRepository
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(oldCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(newCourse, oldCourse, newCourse);

        // Mock behavior for related repositories
        when(courseChapterRepository.findByCourse(oldCourse)).thenReturn(List.of(oldChapter));
        when(courseChapterRepository.save(any(CourseChapter.class))).thenReturn(newChapter);

        when(reportRepository.findByCourse(oldCourse)).thenReturn(List.of(oldReport));
        when(reportRepository.save(any(Report.class))).thenReturn(newReport);

        when(certificateRepository.findByCourse(oldCourse)).thenReturn(oldCertificate);
        when(certificateRepository.save(any(Certificate.class))).thenReturn(newCertificate, oldCertificate);

        when(quizRepository.findByCourse(oldCourse)).thenReturn(oldQuiz);
        when(quizRepository.save(any(Quiz.class))).thenReturn(newQuiz, oldQuiz);

        // Mock behavior for related services
        doNothing().when(courseChapterService).copyCourseChapterDetails(any(), any(), any());
        doNothing().when(reportService).copyReportDetails(any(), any(), any());
        doNothing().when(certificateService).copyCertificateDetails(any(), any(), any());
        doNothing().when(quizService).copyQuizDetails(any(), any(), any());

        // Act
        UpdateCourseResponseDTO updatedCourse = courseService.updateCourse(updateCourseDTO, courseId);

        // Assert
        assertNotNull(updatedCourse);
        assertEquals("Updated Course", updatedCourse.getCourseTittle());
        assertEquals("Updated Category", updatedCourse.getCourseCategory());
        assertEquals(oldCourse.getVersion() + 1, updatedCourse.getVersion());

        // Verify method invocations
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(3)).save(any(Course.class)); // Three saves: old course, new course, updated new course

        verify(courseChapterService, times(1)).copyCourseChapterDetails(any(), any(), any());
        verify(courseChapterRepository, times(2)).save(any(CourseChapter.class));

        verify(reportRepository, times(1)).findByCourse(any(Course.class));
        verify(reportService, times(1)).copyReportDetails(any(), any(), any());
        verify(reportRepository, times(2)).save(any(Report.class));

        verify(certificateRepository, times(1)).findByCourse(any(Course.class));
        verify(certificateService, times(1)).copyCertificateDetails(any(), any(), any());
        verify(certificateRepository, times(2)).save(any(Certificate.class)); // Adjusted to expect 2 saves

        verify(quizRepository, times(1)).findByCourse(any(Course.class));
        verify(quizService, times(1)).copyQuizDetails(any(), any(), any());
        verify(quizRepository, times(2)).save(any(Quiz.class)); // Adjusted to expect 2 saves
    }

    @Test
    public void testUpdateCourse_CourseNotFound() {
        // Arrange
        Integer courseId = 1;
        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO();
        updateCourseDTO.setCourseTittle("Updated Course");
        updateCourseDTO.setCourseCategory("Updated Category");

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> courseService.updateCourse(updateCourseDTO, courseId));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());

        verify(courseRepository, never()).save(any(Course.class)); // Ensure save is never called
        verify(courseChapterService, never()).copyCourseChapterDetails(any(), any(), any());
        verify(courseChapterRepository, never()).save(any(CourseChapter.class));
    }

    @Test
    public void testDeleteCourse_Success() {
        // Arrange
        Integer courseId = 1;
        Course course = new Course();
        course.setId(courseId);
        course.setIsDeleted(false);

        Certificate certificate = new Certificate();
        certificate.setCourse(course);
        certificate.set_Deleted(false);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(certificateRepository.findByCourse(course)).thenReturn(certificate);

        // Act
        DeleteCourseDTO result = courseService.deleteCourse(courseId);

        // Assert
        assertNotNull(result);
        assertEquals("Delete successfully!", result.getMessage());
        assertTrue(result.getIsDeleted());

        verify(courseRepository, times(1)).findById(courseId);
        verify(certificateRepository, times(1)).findByCourse(course);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testDeleteCourse_CourseNotFound() {
        // Arrange
        Integer courseId = 1;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.deleteCourse(courseId);
        });

        assertEquals("The course with id " + courseId + " doesn't exist", exception.getMessage());
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());

        verify(courseRepository, times(1)).findById(courseId);
        verify(certificateRepository, times(0)).findByCourse(any());
        verify(courseRepository, times(0)).save(any());
    }

    @Test
    public void testDeleteCourse_CertificateNotFound() {
        // Arrange
        Integer courseId = 1;
        Course course = new Course();
        course.setId(courseId);
        course.setIsDeleted(false);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(certificateRepository.findByCourse(course)).thenReturn(null);

        // Act & Assert
        CourseException exception = assertThrows(CourseException.class, () -> {
            courseService.deleteCourse(courseId);
        });

        assertEquals("The certificate for course id " + courseId + " doesn't exist", exception.getMessage());
        assertEquals(ErrorCode.CERTIFICATE_NOT_FOUND, exception.getErrorCode());

        verify(courseRepository, times(1)).findById(courseId);
        verify(certificateRepository, times(1)).findByCourse(course);
        verify(courseRepository, times(0)).save(any());
    }

    @Test
    public void testDeleteCourse_UnableToSaveCourse() {
        // Arrange
        Integer courseId = 1;
        Course course = new Course();
        course.setId(courseId);
        course.setIsDeleted(false);

        Certificate certificate = new Certificate();
        certificate.setCourse(course);
        certificate.set_Deleted(false);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(certificateRepository.findByCourse(course)).thenReturn(certificate);
        doThrow(new RuntimeException("Database error")).when(courseRepository).save(course);

        // Act & Assert
        UnableToSaveCourseException exception = assertThrows(UnableToSaveCourseException.class, () -> {
            courseService.deleteCourse(courseId);
        });

        assertEquals("The course is unable to save to database, please re-check it", exception.getMessage());

        verify(courseRepository, times(1)).findById(courseId);
        verify(certificateRepository, times(1)).findByCourse(course);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testGetCoursesByCategory() {
        // Arrange
        String category = "Java";

        Course course1 = new Course();
        course1.setId(1);
        course1.setName("Course 1");
        course1.setCategory("Java");

        Course course2 = new Course();
        course2.setId(2);
        course2.setName("Course 2");
        course2.setCategory("Java");

        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findByCategoryContaining(category)).thenReturn(courses);

        // Act
        List<Course> result = courseService.getCoursesByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getCategory());
        assertEquals("Java", result.get(1).getCategory());
        verify(courseRepository, times(1)).findByCategoryContaining(category);
    }
}


