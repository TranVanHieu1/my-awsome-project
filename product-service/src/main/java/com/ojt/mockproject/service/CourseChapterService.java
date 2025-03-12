package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.CourseChapter.*;
import com.ojt.mockproject.dto.SmallCourseVideo.LectureWithChapterRequestDTO;
import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoResponseDTO;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.CourseChapter;
import com.ojt.mockproject.entity.Lecture;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseChapterRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.SmallCourseVideoRepository;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class CourseChapterService {

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private CourseChapterRepository courseChapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SmallCourseVideoRepository smallCourseVideoRepository;

    public void copyCourseChapterDetails(CourseChapter oldChapter, CourseChapter newChapter, Course newCourse) {
        newChapter.setCourse(newCourse);
        newChapter.setName(oldChapter.getName());
        newChapter.setChapterIndex(oldChapter.getChapterIndex());
        newChapter.setCreateAt(LocalDateTime.now());
        newChapter.setIsDeleted(false);
        newChapter.setIsOld(false);
    }

    public List<CourseChapter> getCourseChapterByCourse(Integer courseId) {

        // Fetch course by ID
        Course course = courseRepository.findCourseById(courseId);
        if (course == null) {
            throw new CourseException("Course not found for ID: " + courseId, ErrorCode.COURSE_NOT_FOUND);
        }

        // Fetch course chapters by course
        List<CourseChapter> courseChapters = courseChapterRepository.findByCourse(course);
        return courseChapters.stream()
                .filter(chapter -> !chapter.getIsDeleted())
                .collect(Collectors.toList());
    }

    public CourseChapterResponseDTO createCourseChapter(CourseChapterRequestDTO courseChapterRequestDTO) {
        CourseChapter courseChapter = new CourseChapter();

        if (courseChapterRequestDTO.getChapterIndex() <= 0) {
            throw new IllegalArgumentException("Chapter must be a positive integer");
        }

        // Fetch course by ID
        Course course = courseRepository.findCourseById(courseChapterRequestDTO.getCourseId());
        if (course == null) {
            throw new CourseException("Course not found for ID: " + courseChapterRequestDTO.getCourseId(), ErrorCode.COURSE_NOT_FOUND);
        }

        // Fetch and update course chapters
        List<CourseChapter> courseChapters = course.getChapters();
        courseChapters.add(courseChapter);
        courseChapter.setCourse(course);
        course.setChapters(courseChapters);

        // Set properties of courseChapter
        courseChapter.setName(courseChapterRequestDTO.getTitle());
        courseChapter.setChapterIndex(courseChapterRequestDTO.getChapterIndex());
        LocalDateTime dateTime = LocalDateTime.now();
        courseChapter.setCreateAt(dateTime);
        courseChapter.setIsDeleted(false);
        courseChapter.setIsOld(false);

        courseChapterRepository.save(courseChapter);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedCreateAt = courseChapter.getCreateAt().format(formatter);

        // Save courseChapter
        return new CourseChapterResponseDTO(courseChapter.getId(),courseChapter.getName(), courseChapter.getChapterIndex(), formattedCreateAt);
    }



    @Transactional
    public ChapterWithLectureResponseDTO courseChapterWithLectures(ChapterWithLectureRequestDTO chapterWithLectureRequestDTO) {
        // Fetch or create CourseChapter
        CourseChapter courseChapter = new CourseChapter();

        if (chapterWithLectureRequestDTO.getChapterIndex() <= 0) {
            throw new IllegalArgumentException("Chapter must be a positive integer");
        }

        Course course = courseRepository.findCourseById(chapterWithLectureRequestDTO.getCourseId());
        if (course == null) {
            throw new CourseException("Course not found for ID: " + chapterWithLectureRequestDTO.getCourseId(), ErrorCode.COURSE_NOT_FOUND);
        }

        courseChapter.setName(chapterWithLectureRequestDTO.getTitle());
        courseChapter.setChapterIndex(chapterWithLectureRequestDTO.getChapterIndex());
        LocalDateTime dateTime = LocalDateTime.now();
        courseChapter.setCreateAt(dateTime);
        courseChapter.setIsDeleted(false);
        courseChapter.setIsOld(false);
        courseChapter.setCourse(course);

        // Save the CourseChapter
        courseChapter = courseChapterRepository.save(courseChapter);

        // Create and save Lectures
        List<SmallVideoResponseDTO> lectureResponseList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (LectureWithChapterRequestDTO lectureDTO : chapterWithLectureRequestDTO.getLectures()) {
            Lecture smallCourseVideo = new Lecture();
            smallCourseVideo.setCourseChapter(courseChapter);
            smallCourseVideo.setName(lectureDTO.getLectureTitle());
            smallCourseVideo.setDescription(lectureDTO.getLectureDescription());
            smallCourseVideo.setThumbnail(lectureDTO.getLectureThumbnail());
            smallCourseVideo.setLink(lectureDTO.getLectureLink());
            smallCourseVideo.setDuration(lectureDTO.getLectureDuration());
            smallCourseVideo.setCreateAt(LocalDateTime.now());
            smallCourseVideo.setIsDeleted(false);

            // Save the Lecture
            smallCourseVideo = smallCourseVideoRepository.save(smallCourseVideo);

            String formattedCreateAt = smallCourseVideo.getCreateAt().format(formatter);
            lectureResponseList.add(new SmallVideoResponseDTO(
                    smallCourseVideo.getId(),
                    smallCourseVideo.getName(),
                    smallCourseVideo.getDescription(),
                    smallCourseVideo.getThumbnail(),
                    smallCourseVideo.getLink(),
                    smallCourseVideo.getDuration(),
                    formattedCreateAt
            ));
        }

        // Format and return response
        String formattedCreateAt = courseChapter.getCreateAt().format(formatter);

        return new ChapterWithLectureResponseDTO(
                courseChapter.getId(),
                courseChapter.getName(),
                courseChapter.getChapterIndex(),
                formattedCreateAt,
                lectureResponseList
        );
    }

    @Transactional
    public ChaptersResponseDTO courseChaptersWithLectures(ChaptersRequestDTO chaptersRequestDTO) {
        // Fetch course by ID
        Course course = courseRepository.findCourseById(chaptersRequestDTO.getCourseId());
        if (course == null) {
            throw new CourseException("Course not found for ID: " + chaptersRequestDTO.getCourseId(), ErrorCode.COURSE_NOT_FOUND);
        }

        List<ChapterWithLectureResponseDTO> responseDTOList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Iterate through the list of chapter requests and create each chapter
        for (ChapterRequestDTO chapterRequestDTO : chaptersRequestDTO.getChapters()) {
            if (chapterRequestDTO.getChapterIndex() <= 0) {
                throw new IllegalArgumentException("Chapter index must be a positive integer");
            }

            CourseChapter courseChapter = new CourseChapter();
            courseChapter.setName(chapterRequestDTO.getTitle());
            courseChapter.setChapterIndex(chapterRequestDTO.getChapterIndex());
            LocalDateTime dateTime = LocalDateTime.now();
            courseChapter.setCreateAt(dateTime);
            courseChapter.setIsDeleted(false);
            courseChapter.setIsOld(false);
            courseChapter.setCourse(course);

            // Save the CourseChapter
            courseChapter = courseChapterRepository.save(courseChapter);

            // Create and save Lectures
            List<SmallVideoResponseDTO> lectureResponseList = new ArrayList<>();
            for (LectureWithChapterRequestDTO lectureDTO : chapterRequestDTO.getLectures()) {
                Lecture lecture = new Lecture();
                lecture.setCourseChapter(courseChapter);
                lecture.setName(lectureDTO.getLectureTitle());
                lecture.setDescription(lectureDTO.getLectureDescription());
                lecture.setThumbnail(lectureDTO.getLectureThumbnail());
                lecture.setLink(lectureDTO.getLectureLink());
                lecture.setDuration(lectureDTO.getLectureDuration());
                lecture.setCreateAt(LocalDateTime.now());
                lecture.setIsDeleted(false);

                // Save the Lecture
                lecture = smallCourseVideoRepository.save(lecture);

                String formattedCreateAt = lecture.getCreateAt().format(formatter);
                lectureResponseList.add(new SmallVideoResponseDTO(
                        lecture.getId(),
                        lecture.getName(),
                        lecture.getDescription(),
                        lecture.getThumbnail(),
                        lecture.getLink(),
                        lecture.getDuration(),
                        formattedCreateAt
                ));
            }

            String formattedCreateAt = courseChapter.getCreateAt().format(formatter);
            responseDTOList.add(new ChapterWithLectureResponseDTO(
                    courseChapter.getId(),
                    courseChapter.getName(),
                    courseChapter.getChapterIndex(),
                    formattedCreateAt,
                    lectureResponseList
            ));
        }

        return new ChaptersResponseDTO(responseDTOList);
    }




    public UpdateCourseChapterDTO updateCourseChapter(CourseChapterRequestDTO courseChapterRequestDTO, Integer id) {

        if (courseChapterRequestDTO.getChapterIndex() <= 0) {
            throw new IllegalArgumentException("Chapter must be a positive integer");
        }

        CourseChapter courseChapter = courseChapterRepository.findCourseChapterById(id);
        if (courseChapter == null) {
            throw new IllegalArgumentException("Course Chapter not found with id: " + id);
        }

        courseChapter.setName(courseChapterRequestDTO.getTitle());
        courseChapter.setChapterIndex(courseChapterRequestDTO.getChapterIndex());
        LocalDateTime dateTime = LocalDateTime.now();
        courseChapter.setUpdateAt(dateTime);
        courseChapterRepository.save(courseChapter);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedUpdateAt = courseChapter.getUpdateAt().format(formatter);
        return new UpdateCourseChapterDTO(courseChapter.getName(), courseChapter.getChapterIndex(), formattedUpdateAt);
    }

    public DeleteCourseChapterDTO deleteCourseChapter(Integer id) {

            CourseChapter courseChapter = courseChapterRepository.findCourseChapterById(id);
            if (courseChapter == null) {
                throw new IllegalArgumentException("Course Chapter not found with id: " + id);
            }

            courseChapter.setIsDeleted(true);
            courseChapterRepository.save(courseChapter);
            return new DeleteCourseChapterDTO("Delete Successful!!", courseChapter.getIsDeleted());
    }
}
