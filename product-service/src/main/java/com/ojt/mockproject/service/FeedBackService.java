package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Feedback.DeleteResponseDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackRequestDTO;
import com.ojt.mockproject.dto.Feedback.FeedBackResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Feedback;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.AccountRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.FeedbackRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedBackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private UploadFileUtils uploadFileUtils;

    private static String message = "There is no feedback!";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Method to format date
    public String formatDate(LocalDateTime date) {
        return date.format(FORMATTER);
    }


    public void copyFeedbackDetails(Feedback oldFeedback, Feedback newFeedback, Course newCourse) {
        newFeedback.setAccount(oldFeedback.getAccount());
        newFeedback.setCourse(newCourse);
        newFeedback.setRating(oldFeedback.getRating());
        newFeedback.setDescription(oldFeedback.getDescription());
        newFeedback.setCreateAt(LocalDateTime.now());
        newFeedback.setIsDeleted(false);
    }


    //Create feed back
    public FeedBackResponseDTO addFeedBack(FeedBackRequestDTO feedBackRequestDTO) throws Exception {
        //get current user
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }
        //Get information of course
        Course course = courseRepository.findById(feedBackRequestDTO.getCourseID()).get();

        if(course == null || course.getIsDeleted() == true){
            throw new CourseException("Course not found!", ErrorCode.COURSE_NOT_FOUND);
        }

        //validate rating
        if (feedBackRequestDTO.getRating() < 0 || feedBackRequestDTO.getRating() > 5) {
            throw new Exception("Please rating from 1 to 5");
        }

        //insert feed back to database
        Feedback feedback = new Feedback(LocalDateTime.now(), feedBackRequestDTO.getDescrip(), feedBackRequestDTO.getRating(), account, course);
        String formattedCreateAt = formatDate(feedback.getCreateAt());
        //save info to database
        try{
            feedbackRepository.save(feedback);
        }catch (Exception ex){
            throw new RuntimeException("Can not create feedback!");
        }
        //get avatar account
        String imgUrl = account.getAvatar();
        if(imgUrl == null || imgUrl.isEmpty()){
            imgUrl = "logog.png";
        }

        return new FeedBackResponseDTO(feedback.getId(), uploadFileUtils.getSignedAvatarUrl(imgUrl), account.getName(), course.getName(), feedback.getDescription(), feedback.getRating(), formattedCreateAt);

    }

    //Delete feed back by id  and account
    public DeleteResponseDTO deleteFeedBack(Integer id) throws Exception {
        //get current user
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }
        // find the feedback by id and account
        Feedback feedback = feedbackRepository.findFeedbacksByIdAndAccount(id, account);
        if (feedback == null) {
            throw new RuntimeException("Feedback not found or you do not have permission to delete it");
        }

        feedback.setIsDeleted(true);
        // save feedback
        try {
            feedbackRepository.save(feedback);
            return new DeleteResponseDTO("Delete successfully!", "");
        } catch (Exception ex) {
            throw new Exception("Delete fail!");
        }

    }

    //Get All feed back
    public List<FeedBackResponseDTO> getAllFeedBack() throws Exception {
        List<FeedBackResponseDTO> returnList = new ArrayList<>();
        try {
            List<Feedback> list = feedbackRepository.findAll().stream()
                    .filter(feedback -> !feedback.getIsDeleted() && !feedback.getCourse().getIsDeleted())
                    .collect(Collectors.toList());

            for (Feedback listFeedBack : list) {
                String formattedCreateAt = formatDate(listFeedBack.getCreateAt());
                String imgUrl = listFeedBack.getAccount().getAvatar();
                if(imgUrl == null || imgUrl.isEmpty()){
                    imgUrl = "logo.png";
                }
                returnList.add(new FeedBackResponseDTO(
                        listFeedBack.getId(),
                        uploadFileUtils.getSignedAvatarUrl(imgUrl),
                        listFeedBack.getAccount().getName(),
                        listFeedBack.getCourse().getName(),
                        listFeedBack.getDescription(),
                        listFeedBack.getRating(),
                        formattedCreateAt));
            }
            if (returnList.isEmpty()) {
                throw new Exception(message);
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());

        }
        return returnList;
    }

    //Get feedback by course
    public List<FeedBackResponseDTO> getFeebackByCourse(Integer id) throws Exception {
        List<FeedBackResponseDTO> returnList = new ArrayList<>();
        try {

            List<Feedback> listFeedback = feedbackRepository.findFeedbacksByCourseId(id).stream()
                    .filter(feedback -> !feedback.getIsDeleted() && !feedback.getCourse().getIsDeleted())
                    .collect(Collectors.toList());
            for (Feedback feedback: listFeedback) {
                String formattedCreateAt = formatDate(feedback.getCreateAt());
                String imgUrl = feedback.getAccount().getAvatar();
                if(imgUrl == null || imgUrl.isEmpty()){
                    imgUrl = "logo.png";
                }
                returnList.add(new FeedBackResponseDTO(
                        feedback.getId(),
                        uploadFileUtils.getSignedAvatarUrl(imgUrl),
                        feedback.getAccount().getName(),
                        feedback.getCourse().getName(),
                        feedback.getDescription(),
                        feedback.getRating(),
                        formattedCreateAt));
            }

            if (returnList.isEmpty()) {
                throw new Exception(message);
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());

        }
        return returnList;
    }



}
