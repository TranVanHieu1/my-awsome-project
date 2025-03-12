package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Course.*;
import com.ojt.mockproject.entity.*;
import com.ojt.mockproject.entity.Enum.AccountRoleEnum;
import com.ojt.mockproject.entity.Enum.AccountStatusEnum;
import com.ojt.mockproject.entity.certificate_quiz.Certificate;
import com.ojt.mockproject.entity.certificate_quiz.Quiz;
import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.entity.Enum.CourseStatusEnum;
import com.ojt.mockproject.exceptionhandler.course.UnableToSaveCourseException;
import com.ojt.mockproject.repository.*;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.StringUtil;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private CourseChapterRepository courseChapterRepository;

    @Autowired
    @Lazy
    private CrashCourseVideoRepository crashCourseVideoRepository;

    @Autowired
    @Lazy
    private CourseChapterService courseChapterService;

    @Autowired
    private SmallCourseVideoRepository smallCourseVideoRepository;

    @Autowired
    private SmallCourseVideoService smallCourseVideoService;
    @Autowired
    @Lazy
    private CrashCourseVideoService crashCourseVideoService;

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CertificateRepository certificateRepository;


    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    @Lazy
    private QuizService quizService;

    @Autowired
    private UploadFileUtils uploadFileUtils;

    @Autowired
    private StringUtil stringUtils;

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id).orElseThrow(() -> new CourseException(("The course with id " + id + "doesn't exist"), ErrorCode.COURSE_NOT_FOUND));
    }

    public void saveCourse(Course course) {
        try {
            courseRepository.save(course);
        } catch (Exception e) {
            throw new UnableToSaveCourseException("The course is unable to save to database, please re-check it");
        }
    }

    public List<CourseDTO> getAllCourses() {
        //Get All Course From Database
        List<Course> listCourse = courseRepository.findAll().stream()
                .filter(course1 -> !course1.getIsDeleted() == true && course1.getStatus().equals(CourseStatusEnum.APPROVED))
                .collect(Collectors.toList());
        List<CourseDTO> list = new ArrayList<>();

        Integer duration;
        Double rating;
        for (Course course : listCourse) {

            //get image of course
            String signedImageUrl = null;
            for (Media media : course.getCrashCourseVideos()) {
                try {
                    signedImageUrl = uploadFileUtils.getSignedImageUrl(media.getThumbnail());
                } catch (Exception e) {
                    throw new AccountException(e.getMessage());
                }
            }

            //Calculate rating of the course
            rating = 0.0;
            for (Feedback feedback : course.getFeedbacks()) {
                rating += feedback.getRating();
            }
            if (rating != 0.0) {
                rating = rating / course.getFeedbacks().size();
            }


            //Calculate duration of the course
            duration = 0;
            for (CourseChapter courseChapter : course.getChapters()) {
                for (Lecture lecture : courseChapter.getSmallCourseVideos()) {
                    duration += lecture.getDuration();
                }
            }

            //Add information to list
            int view = 0;
            if (course.getView() != null) {
                view = course.getView();
            }
            list.add(new CourseDTO(
                    course.getId(),
                    course.getName(),
                    course.getAccount().getName(),
                    course.getCategory(),
                    course.getShortDescription(),
                    course.getRequirements(),
                    course.getPrice(),
                    rating,
                    feedBackService.formatDate(course.getCreateAt()),
                    duration,
                    course.getDescription(),
                    signedImageUrl,
                    view,
                    course.getStudentWillLearn(),
                    course.getAudioLanguage()));

        }

        if (list.isEmpty()) {
            throw new CourseException("No course", ErrorCode.COURSE_NOT_FOUND);
        }
        return list;
    }

    public List<CourseDTO> getCourseByName(String name) {
        List<Course> listCourse = courseRepository.findByNameContainingIgnoreCase(name).stream()
                .filter(course1 -> !course1.getIsDeleted() == true && course1.getStatus().equals(CourseStatusEnum.APPROVED))
                .collect(Collectors.toList());
        List<CourseDTO> list = new ArrayList<>();

        Integer duration = 0;
        double rating;
        for (Course course : listCourse) {
            //get image of course
            String signedImageUrl = null;
            for (Media media : course.getCrashCourseVideos()) {
                try {
                    signedImageUrl = uploadFileUtils.getSignedImageUrl(media.getThumbnail());
                } catch (Exception e) {
                    throw new AccountException(e.getMessage());
                }
            }
            //Calculate rating of the course
            rating = 0;
            for (Feedback feedback : course.getFeedbacks()) {
                rating += feedback.getRating();
            }
            if (rating != 0.0) {
                rating = rating / course.getFeedbacks().size();
            }

            //Calculate duration of the course
            duration = 0;
            for (CourseChapter courseChapter : course.getChapters()) {
                for (Lecture lecture : courseChapter.getSmallCourseVideos()) {
                    duration += lecture.getDuration();
                }
            }

            //Add information to list
            int view = 0;
            if (course.getView() != null) {
                view = course.getView();
            }
            list.add(new CourseDTO(
                    course.getId(),
                    course.getName(),
                    course.getAccount().getName(),
                    course.getCategory(),
                    course.getShortDescription(),
                    course.getRequirements(),
                    course.getPrice(),
                    rating,
                    feedBackService.formatDate(course.getCreateAt()),
                    duration,
                    course.getDescription(),
                    signedImageUrl,
                    view,
                    course.getStudentWillLearn(),
                    course.getAudioLanguage()));

        }

        if (list.isEmpty()) {
            throw new CourseException("No course", ErrorCode.COURSE_NOT_FOUND);
        }
        return list;
    }


    //get course by id
    public CourseDTO viewCourseById(Integer Id) throws IOException {
        Course course = getCourseById(Id);
        if (course == null || course.getIsDeleted() == true) {
            throw new CourseException("Course not found", ErrorCode.COURSE_NOT_FOUND);
        }


        Integer duration;
        double rating;

        String signedImageUrl = null;
        if (course.getCrashCourseVideos().isEmpty()) {
            signedImageUrl = "";
        } else {
            signedImageUrl = uploadFileUtils.getSignedImageUrl(course.getCrashCourseVideos().get(0).getThumbnail());
        }

        //Calculate rating of the course
        rating = 0;
        for (Feedback feedback : course.getFeedbacks()) {
            rating += feedback.getRating();
        }
        if (rating != 0.0) {
            rating = rating / course.getFeedbacks().size();
        }

        //Calculate duration of the course
        duration = 0;
        for (CourseChapter courseChapter : course.getChapters()) {
            for (Lecture lecture : courseChapter.getSmallCourseVideos()) {
                duration += lecture.getDuration();
            }
        }

        // Update the course view count and save it
        course.setView(course.getView() + 1);
        courseRepository.save(course);

        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getAccount().getName(),
                course.getCategory(),
                course.getShortDescription(),
                course.getRequirements(),
                course.getPrice(),
                rating,
                feedBackService.formatDate(course.getCreateAt()),
                duration,
                course.getDescription(),
                signedImageUrl,
                course.getView(),
                course.getStudentWillLearn(),
                course.getAudioLanguage());
    }


    //create course and certificate here
    public List<FindCourseResponseDTO> getCourseByAccount(CourseStatusEnum status) {
        //get current user
        Account account = null;
        List<FindCourseResponseDTO> list = new ArrayList<>();
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }

        List<Course> listCourse = courseRepository.findByAccountAndStatus(account, status).stream()
                .filter(course1 -> !course1.getIsDeleted()==true)
                .collect(Collectors.toList());


        for (Course course : listCourse) {

            List<Integer> studentsList = course.getPurchasedStudents() == null ? new ArrayList<>() : stringUtils.stringToList(course.getPurchasedStudents());
            int sales = studentsList.size();
            int parts = course.getChapters().size();
            String formattedCreateAt = feedBackService.formatDate(course.getCreateAt());
            list.add(new FindCourseResponseDTO(course.getId(), course.getName(), formattedCreateAt, sales, parts, course.getCategory(), course.getStatus()));

        }

        return list;
    }

    public CreateCourseResponseDTO createCourse(CourseRequestDTO course) {

        try {
            Account account = null;

            account = accountUtils.getCurrentAccount();

            if (course.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price cannot be a negative number");
            }

            if (!account.getStatus().equals(AccountStatusEnum.APPROVED)) {
                throw new AccountException("Account is not an Instructor: " + account.getId(), ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
            }

            if (!account.getRole().equals(AccountRoleEnum.INSTRUCTOR) ) {
                throw new AccountException("Account is not an Instructor: " + account.getId(), ErrorCode.ACCOUNT_NOT_INSTRUCTOR);
            }

            LocalDateTime dateTime = LocalDateTime.now();
            Course course1 = new Course();
            course1.setName(course.getBasicCourseTittle());
            course1.setCategory(course.getBasicCourseCategory());
            course1.setDescription(course.getBasicCourseDescription());
            course1.setShortDescription(course.getBasicShortDescription());
            course1.setStudentWillLearn(course.getBasicStudentWillLearn());
            course1.setRequirements(course.getBasicRequirements());
            course1.setAudioLanguage(course.getBasicAudioLanguage());
            course1.setPrice(course.getPrice());
            course1.setIsDeleted(false);
            course1.setCreateAt(dateTime);
            course1.setUpdateAt(dateTime);
            course1.setVersion(1);
            course1.setStatus(CourseStatusEnum.PENDING);
            course1.setAccount(account);
            course1.setIsOld(false);
            course1.setView(0);
            courseRepository.save(course1);

            //create certificate

            Certificate certificate1 = new Certificate();
            certificate1.setName(course.getBasicCourseTittle());
            certificate1.setCourse(course1);
            certificate1.setDescription(course.getBasicCourseDescription());
            certificate1.setCreateAt(dateTime);
            certificateRepository.save(certificate1);

            //Create quiz for course

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedCreateAt = course1.getCreateAt().format(formatter);

            return new CreateCourseResponseDTO(course1.getId(), course1.getCategory(), formattedCreateAt, course1.getName(), course1.getStatus(), course1.getPrice(), course1.getDescription(), course1.getShortDescription(), course1.getStudentWillLearn(), course1.getRequirements(), course1.getAudioLanguage());
        } catch (ClassCastException e) {
            throw new CourseException("Login to create new course", ErrorCode.NOT_LOGIN);
        } catch (AccountException e) {
            throw new AccountException("Account is not an Instructor", ErrorCode.ACCOUNT_NOT_INSTRUCTOR);

        }

    }

    private void copyCourseDetails(Course oldCourse, Course newCourse) {
        newCourse.setName(oldCourse.getName());
        newCourse.setCategory(oldCourse.getCategory());
        newCourse.setDescription(oldCourse.getDescription());
        newCourse.setPrice(oldCourse.getPrice());
        newCourse.setIsDeleted(false);
        newCourse.setCreateAt(LocalDateTime.now());
        newCourse.setUpdateAt(LocalDateTime.now());
        newCourse.setVersion(oldCourse.getVersion() + 1);
        newCourse.setStatus(CourseStatusEnum.PENDING);
        newCourse.setAccount(oldCourse.getAccount());
        newCourse.setIsOld(false);
    }

    private void duplicateRelatedEntities(Course oldCourse, Course newCourse) {
        // Duplicate CourseChapter
        List<CourseChapter> oldChapters = courseChapterRepository.findByCourse(oldCourse);
        for (CourseChapter oldChapter : oldChapters) {
            CourseChapter newChapter = new CourseChapter();
            courseChapterService.copyCourseChapterDetails(oldChapter, newChapter, newCourse);
            newChapter = courseChapterRepository.save(newChapter);

            oldChapter.setIsOld(true);
            oldChapter.setIsDeleted(true);

            courseChapterRepository.save(oldChapter);

            // Duplicate SmallCourseVideo
            List<Lecture> oldVideos = smallCourseVideoRepository.findByCourseChapter(oldChapter);
            for (Lecture oldVideo : oldVideos) {
                Lecture newVideo = new Lecture();
                smallCourseVideoService.copySmallCourseVideoDetails(oldVideo, newVideo, newChapter);
                smallCourseVideoRepository.save(newVideo);

                oldVideo.setIsDeleted(true);
                smallCourseVideoRepository.save(oldVideo);
            }
        }

        // Duplicate CrashCourseVideo
        List<Media> oldCrashVideos = crashCourseVideoRepository.findByCourse(oldCourse);
        for (Media oldCrashVideo : oldCrashVideos) {
            Media newCrashVideo = new Media();
            crashCourseVideoService.copyCrashCourseVideoDetails(oldCrashVideo, newCrashVideo, newCourse);
            crashCourseVideoRepository.save(newCrashVideo);

            oldCrashVideo.setIsDeleted(true);
            oldCrashVideo.setIsOld(true);

            crashCourseVideoRepository.save(oldCrashVideo);
        }

        // Duplicate feedback
        List<Feedback> oldFeedbacks = feedbackRepository.findByCourse(oldCourse);
        for (Feedback oldFeedback : oldFeedbacks) {
            Feedback newFeedback = new Feedback();
            feedBackService.copyFeedbackDetails(oldFeedback, newFeedback, newCourse);

            feedbackRepository.save(newFeedback);

            oldFeedback.setIsDeleted(true);
            feedbackRepository.save(oldFeedback);

        }


        // Duplicate report
        List<Report> oldReports = reportRepository.findByCourse(oldCourse);
        for (Report oldReport : oldReports) {
            Report newReport = new Report();
            reportService.copyReportDetails(oldReport, newReport, newCourse);

            reportRepository.save(newReport);

            oldReport.setIsDeleted(true);
            reportRepository.save(oldReport);

        }

        //Duplicate certificate
        Certificate oldCertificate = certificateRepository.findByCourse(oldCourse);
        Certificate newCertificate = new Certificate();
        certificateService.copyCertificateDetails(oldCertificate, newCertificate, newCourse);
        certificateRepository.save(newCertificate);
        oldCertificate.set_Deleted(true);
        certificateRepository.save(oldCertificate);

        //Duplicate Quiz
        Quiz oldQuiz = quizRepository.findByCourse(oldCourse);
        Quiz newQuiz = new Quiz();
        quizService.copyQuizDetails(oldQuiz, newQuiz, newCourse);
        quizRepository.save(newQuiz);
        oldQuiz.set_Deleted(true);
        quizRepository.save(oldQuiz);
    }

    public UpdateCourseResponseDTO updateCourse(UpdateCourseDTO updateCourseDTO, Integer id) {

        // Find the existing course
        Course oldCourse = courseRepository.findById(id).orElseThrow(() -> new CourseException(("The course with id " + id + " doesn't exist"), ErrorCode.COURSE_NOT_FOUND));

        // Duplicate the course
        Course newCourse = new Course();
        copyCourseDetails(oldCourse, newCourse);
        newCourse = courseRepository.save(newCourse);

        // Update old course
        List<Integer> oldVersionList = oldCourse.getOldVersionList();
        if (oldVersionList == null) {
            oldVersionList = new ArrayList<>(); // Initialize if null
        } else {
            oldVersionList = new ArrayList<>(oldVersionList); // Ensure it's a mutable list
        }
        oldVersionList.add(newCourse.getId());
        oldCourse.setOldVersionList(oldVersionList);
        oldCourse.setIsDeleted(true);
        oldCourse.setIsOld(true);
        courseRepository.save(oldCourse);

        // Duplicate related entities
        duplicateRelatedEntities(oldCourse, newCourse);

        // Update new course with new details or old details if new details are not provided
        newCourse.setName(updateCourseDTO.getCourseTittle() != null ? updateCourseDTO.getCourseTittle() : oldCourse.getName());
        newCourse.setCategory(updateCourseDTO.getCourseCategory() != null ? updateCourseDTO.getCourseCategory() : oldCourse.getCategory());
        newCourse.setPrice(updateCourseDTO.getPrice() != null ? updateCourseDTO.getPrice() : oldCourse.getPrice());
        newCourse.setDescription(updateCourseDTO.getCourseDescription() != null ? updateCourseDTO.getCourseDescription() : oldCourse.getDescription());
        newCourse.setShortDescription(updateCourseDTO.getShortDescription() != null ? updateCourseDTO.getShortDescription() : oldCourse.getShortDescription());
        newCourse.setRequirements(updateCourseDTO.getRequirements() != null ? updateCourseDTO.getRequirements() : oldCourse.getRequirements());
        newCourse.setAudioLanguage(updateCourseDTO.getAudioLanguage() != null ? updateCourseDTO.getAudioLanguage() : oldCourse.getAudioLanguage());
        newCourse.setStudentWillLearn(updateCourseDTO.getStudentWillLearn() != null ? updateCourseDTO.getStudentWillLearn() : oldCourse.getStudentWillLearn());
        newCourse.setUpdateAt(LocalDateTime.now());
        newCourse.setVersion(oldCourse.getVersion() + 1);
        newCourse.setIsOld(false);
        newCourse.setIsDeleted(false);

        courseRepository.save(newCourse);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedUpdateAt = newCourse.getUpdateAt().format(formatter);
        return new UpdateCourseResponseDTO(newCourse.getName(), newCourse.getCategory(), newCourse.getDescription(), newCourse.getPrice(), formattedUpdateAt, newCourse.getVersion(), newCourse.getShortDescription(), newCourse.getStudentWillLearn(), newCourse.getRequirements(), newCourse.getAudioLanguage());
    }


    public DeleteCourseDTO deleteCourse(Integer id) {

        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseException(("The course with id " + id + " doesn't exist"), ErrorCode.COURSE_NOT_FOUND));
        course.setIsDeleted(true);


        Certificate certificate = certificateRepository.findByCourse(course);

        if (certificate == null) {
            throw new CourseException("The certificate for course id " + id + " doesn't exist", ErrorCode.CERTIFICATE_NOT_FOUND);
        }

        certificate.set_Deleted(true);

        try {
            courseRepository.save(course);
            certificateRepository.save(certificate);
        } catch (Exception e) {
            throw new UnableToSaveCourseException("The course is unable to save to database, please re-check it");
        }
        return new DeleteCourseDTO("Delete successfully!", course.getIsDeleted());
    }

    /*
    Browse all Category fields in the course table.
    Then returns a list of categories within the entire course.
    However, overlapping categories will only be taken once.
    */


    public List<Course> getCoursesByCategory(String category) {
        // Find courses by category
        return courseRepository.findByCategoryContaining(category);
    }

    public List<CourseDTO> getCoursesByAccount(Integer accountId) {
        //get current user
        Account account = null;
        try {
            account = accountService.findById(accountId);
        } catch (Exception ex) {
            throw new AccountException("The account with id " + accountId + " doesn't exist!");
        }

        List<Course> listCourse = courseRepository.findCourseByAccount(account).stream()
                .filter(course1 -> !course1.getIsDeleted() == true && course1.getStatus().equals(CourseStatusEnum.APPROVED))
                .collect(Collectors.toList());
        List<CourseDTO> list = new ArrayList<>();

        Integer duration;
        Double rating;
        for (Course course : listCourse) {

            //get image of course
            String signedImageUrl = null;
            for (Media media : course.getCrashCourseVideos()) {
                try {
                    signedImageUrl = uploadFileUtils.getSignedImageUrl(media.getThumbnail());
                } catch (Exception e) {
                    throw new AccountException(e.getMessage());
                }
            }

            //Calculate rating of the course
            rating = 0.0;
            for (Feedback feedback : course.getFeedbacks()) {
                rating += feedback.getRating();
            }
            if (rating != 0.0) {
                rating = rating / course.getFeedbacks().size();
            }


            //Calculate duration of the course
            duration = 0;
            for (CourseChapter courseChapter : course.getChapters()) {
                for (Lecture lecture : courseChapter.getSmallCourseVideos()) {
                    duration += lecture.getDuration();
                }
            }

            //Add information to list
            int view = 0;
            if (course.getView() != null) {
                view = course.getView();
            }
            list.add(new CourseDTO(
                    course.getId(),
                    course.getName(),
                    course.getAccount().getName(),
                    course.getCategory(),
                    course.getShortDescription(),
                    course.getRequirements(),
                    course.getPrice(),
                    rating,
                    feedBackService.formatDate(course.getCreateAt()),
                    duration,
                    course.getDescription(),
                    signedImageUrl,
                    view,
                    course.getStudentWillLearn(),
                    course.getAudioLanguage()));

        }

        if (list.isEmpty()) {
            throw new CourseException("No course", ErrorCode.COURSE_NOT_FOUND);
        }
        return list;
    }
}


