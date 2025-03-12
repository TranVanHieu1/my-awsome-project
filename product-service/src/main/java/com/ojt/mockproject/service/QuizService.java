package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Quiz.*;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.certificate_quiz.Question;
import com.ojt.mockproject.entity.certificate_quiz.QuestionChoice;
import com.ojt.mockproject.entity.certificate_quiz.Quiz;
import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import com.ojt.mockproject.exceptionhandler.course.UnableToSaveCourseException;
import com.ojt.mockproject.exceptionhandler.quiz.CannotGetQuizExeption;
import com.ojt.mockproject.exceptionhandler.quiz.UnableToGetInformationFromQuiz;
import com.ojt.mockproject.exceptionhandler.quiz.UnableToSaveQuestionToQuiz;
import com.ojt.mockproject.repository.QuestionChoiceRepository;
import com.ojt.mockproject.repository.QuestionRepository;
import com.ojt.mockproject.repository.QuizRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {

    @Lazy
    @Autowired
    private CourseService courseService;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UploadFileUtils uploadFileUtils;
    @Autowired
    private AccountUtils accountUtils;
    @Autowired
    private QuestionChoiceRepository questionChoiceRepository;

    public void copyQuizDetails(Quiz oldQuiz, Quiz newQuiz, Course newCourse) {
        newQuiz.setCourse(newCourse);
        newQuiz.setTitle(oldQuiz.getTitle());
        newQuiz.setDescription(oldQuiz.getDescription());
        newQuiz.setCreateAt(LocalDateTime.now());
        newQuiz.set_Deleted(false);
    }

    public QuizRequestDTO createQuizForCourse(QuizRequestDTO quizRequestDTO, Integer courseId) {
        //get current user
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
            if (account == null) {
                throw new NotLoginException("Not Login");
            }
        } catch (NotLoginException ex) {
            throw new NotLoginException("Not Login");
        }

        //Get Quiz
        Quiz quiz = null;
        try {
            quiz = new Quiz();
            quiz.setTitle(quizRequestDTO.getQuizTitle());
            quiz.setDescription(quizRequestDTO.getQuizDescription());
            quiz.setCreateAt(LocalDateTime.now());
            quiz.set_Deleted(false);
        } catch (Exception e) {
            throw new CannotGetQuizExeption("Unable to get quiz from request, please re-check if quiz's title and description is correct!");
        }

        List<Question> questionList = new ArrayList<>();
        List<QuestionChoice> questionChoiceList = null;
        //Get question list

        //Get Score for each question
        double score = 0;
        try {
            score = ((double) 100 / quizRequestDTO.getQuestionInputList().size());
        } catch (Exception e) {
            throw new UnableToGetInformationFromQuiz("Unable to get question list in quiz! Please re-check!");
        }

        try {
            for (QuestionInput questionInput : quizRequestDTO.getQuestionInputList()) {

                //Get every question from list
                Question question = new Question();
                question.setQuiz(quiz);
                question.setQuestionText(questionInput.getQuestionTitle());
                question.setImage(questionInput.getQuestionImage());
                question.setCreateAt(LocalDateTime.now());
                question.setScore(score);
                question.set_Deleted(false);

                //Get choice list and add to question
                questionChoiceList = new ArrayList<>();
                for (ChoiceInput choiceInput : questionInput.getChoiceInputList()) {
                    QuestionChoice questionChoice = new QuestionChoice(
                            question,
                            choiceInput.getQuestionOptionTitle(),
                            choiceInput.isOptionCorrect(),
                            LocalDateTime.now()
                    );
                    //Add choice to list
                    questionChoiceList.add(questionChoice);
                }

                //Add choice list to question
                question.setQuestionChoices(questionChoiceList);

                //Add question to list
                question.setQuiz(quiz);
                questionList.add(question);
            }
        } catch (Exception e) {
            throw new CannotGetQuizExeption("Unable to get question from request, please re-check if quiz's title and description is correct! Instance at QuizService.createQuizForCourse.");
        }

        try {
            //Add question list to quiz
            quiz.setQuestions(questionList);
        } catch (Exception e) {
            throw new UnableToSaveQuestionToQuiz("Unable to save questions to quiz! Please re-check!");
        }

        //Add quiz to course
        try {
            Course course = courseService.getCourseById(courseId);
            course.setQuiz(quiz);
            quiz.setCourse(course);
            courseService.saveCourse(course);
        } catch (Exception e) {
            throw new UnableToSaveCourseException("Unable to save course to database! Instance at QuizService.createQuizForCourse.\n " +
                    "Key (course_id)=("+courseId+") already exists a quiz for it");
        }
        return quizRequestDTO;
    }

    public QuizResponseDTO takeQuizByCourseId(Integer courseId) throws IOException {
        //Take the course by course ID
        Course currentCourse = courseService.getCourseById(courseId);

        //Create Quiz Response
        QuizResponseDTO quizResponseDTO = new QuizResponseDTO();

        //Put quiz's name into quiz response
        Quiz currentQuiz = null;
        try {
            currentQuiz = quizRepository.findByCourse(currentCourse);
            quizResponseDTO.setCourseId(currentCourse.getId());
            quizResponseDTO.setQuizTitle(currentQuiz.getTitle());
        } catch (Exception e) {
            throw new CannotGetQuizExeption("This course has no quiz data, please try another course");
        }
        //Put quiz's number of question into quiz response
        List<Question> questionList = null;
        try {
            questionList = questionRepository.findQuestionsByQuiz(currentQuiz);
        } catch (Exception e) {
            throw new CannotGetQuizExeption("This course has no question data, please try another course");
        }
        quizResponseDTO.setNumberOfQuestion(questionList.size());

        //Create question response for quiz response
        List<QuestionOutput> questionOutputList = new ArrayList<>();
        List<ChoiceOutput> choiceOutputList = null;

        for (Question question : questionList) {
            //Change data from question to questionOutput
            QuestionOutput questionOutput = new QuestionOutput();
            questionOutput.setQuestionId(question.getId());
            questionOutput.setQuestionText(question.getQuestionText());
            if (!question.getImage().isEmpty()) {
                questionOutput.setQuestionImage(uploadFileUtils.getSignedImageUrl(question.getImage()));
            } else {
                questionOutput.setQuestionImage("");
            }

            //Get choice question's choice list
            choiceOutputList = new ArrayList<>();
            for (QuestionChoice questionChoice : question.getQuestionChoices()) {
                ChoiceOutput choiceOutput = new ChoiceOutput(
                        questionChoice.getId(),
                        questionChoice.getAnswer()
                );
                choiceOutputList.add(choiceOutput);
            }

            //Add to question response list
            questionOutput.setChoiceOutputList(choiceOutputList);
            questionOutputList.add(questionOutput);
        }

        quizResponseDTO.setQuestionOutputList(questionOutputList);

        return quizResponseDTO;
    }


    //Take submit list and sort by QuestionID
    //Take correct list and sort by QuestionID
    //Compare both of them with Set
    //If any of the body in Set Submit is differ from Set Correct -> It will be removed for Set Submit
    public SubmitQuizResponseDTO submitsubmitQuizTest(SubmitQuizRequestDTO submitQuizRequestDTO) {
        //Get the current login account
        Account account = null;
        try {
            account = accountUtils.getCurrentAccount();
        } catch (Exception ex) {
            throw new NotLoginException("Not Login");
        }

        //Get list question from quiz
        List<AnswerListInput> submitResultList = submitQuizRequestDTO.getAnswerList();
        //Sort it by questionId
        submitResultList.sort(Comparator.comparing(AnswerListInput::getQuestionId));

        //Get question and correct answer from database
        List<AnswerListInput> correctList = new ArrayList<>();
        for (int i = 0; i < submitResultList.size(); i++) {
            Question question = questionRepository.findById(submitResultList.get(i).getQuestionId()).get();
            QuestionChoice questionChoice = questionChoiceRepository.findQuestionChoiceByQuestionAndIsCorrectTrue(question).orElse(null);
            AnswerListInput answerListInput = new AnswerListInput(question.getId(), questionChoice.getId());
            correctList.add(answerListInput);
        }

        //Sort it by id
        correctList.sort(Comparator.comparing(AnswerListInput::getQuestionId));

        // Convert list a to a set
        Set<AnswerListInput> setSubmit = new HashSet<>(submitResultList);
        Set<AnswerListInput> setCorrect = new HashSet<>(correctList);

        //Compare
        setSubmit.retainAll(setCorrect);

        //Get total of question
        List<Question> questionList = null;
        try {
            questionList = questionRepository.findQuestionsByQuiz(quizRepository.findById(submitQuizRequestDTO.getQuizId()).get());
        } catch (Exception e) {
            throw new UnableToGetInformationFromQuiz("Unable to get question list in quiz! Please re-check!");
        }

        //Get response information
        int correct = setSubmit.size();
        int wrong = setCorrect.size() - setSubmit.size();
        int total = questionList.size();

        //Save the result to database
        //If there's already exist a result, replace them with the newer one

        return new SubmitQuizResponseDTO(correct, wrong, total, account.getName());
    }

}
