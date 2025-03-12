package com.ojt.mockproject.service;


import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseRequestDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.CrashCourseResponseDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.DeleteCrashCourseDTO;
import com.ojt.mockproject.dto.CrashCourseVideo.UpdateCrashCourseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.Media;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.repository.CrashCourseVideoRepository;
import com.ojt.mockproject.utils.UploadFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CrashCourseVideoService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CrashCourseVideoRepository crashCourseVideoRepository;

    @Autowired
    private UploadFileUtils uploadFileUtils;

    public void copyCrashCourseVideoDetails(Media oldVideo, Media newVideo, Course newCourse) {
        newVideo.setCourse(newCourse);

        newVideo.setLink(oldVideo.getLink());
        newVideo.setThumbnail(oldVideo.getThumbnail());
        newVideo.setDuration(oldVideo.getDuration());
        newVideo.setCreateAt(LocalDateTime.now());
        newVideo.setIsDeleted(false);newVideo.setIsOld(false);
    }

    public List<Media> getCrashCourseVideoByCourse(Integer courseId) {
        Course course = courseRepository.findCourseById(courseId);

        if (course == null) {
            throw new CourseException("Course not found for ID: " + courseId, ErrorCode.COURSE_NOT_FOUND);
        }

        List<Media> crashCourseVideos = crashCourseVideoRepository.findByCourse(course);
        return crashCourseVideos.stream()
                .filter(video -> !video.getIsDeleted())
                .collect(Collectors.toList());
    }


    public CrashCourseResponseDTO createCrashCourseVideo(CrashCourseRequestDTO crashCourseRequestDTO, String url, Account account) {
        if (crashCourseRequestDTO.getMediaDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer");
        }
        Media crashCourseVideo = new Media();
        // Fetch course by ID
        Course course = courseRepository.findCourseById(crashCourseRequestDTO.getCourseId());
        if (course == null) {
            throw new CourseException("Course not found for ID: " + crashCourseRequestDTO.getCourseId(), ErrorCode.COURSE_NOT_FOUND);
        }

        int maxWidthSizeImage = 1000;
        String signedImageUrl = null;
        String imageUrl = null;
        String folderName = account.getEmail() + url;
        if (crashCourseRequestDTO.getMediaThumnail() == null || crashCourseRequestDTO.getMediaThumnail().isEmpty()) {
            imageUrl = null;
        } else {
            try {
                imageUrl = uploadFileUtils.uploadFile(folderName, crashCourseRequestDTO.getMediaThumnail(), maxWidthSizeImage);
                signedImageUrl = uploadFileUtils.getSignedImageUrl(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Media> crashCourseVideos = course.getCrashCourseVideos();
        crashCourseVideos.add(crashCourseVideo);
        crashCourseVideo.setCourse(course);
        course.setCrashCourseVideos(crashCourseVideos);
        crashCourseVideo.setLink(crashCourseRequestDTO.getMediaLink());
        crashCourseVideo.setDuration(crashCourseRequestDTO.getMediaDuration());
        crashCourseVideo.setThumbnail(imageUrl);
        LocalDateTime dateTime = LocalDateTime.now();
        crashCourseVideo.setCreateAt(dateTime);
        crashCourseVideo.setIsDeleted(false);
        crashCourseVideo.setIsOld(false);
        crashCourseVideoRepository.save(crashCourseVideo);

        course.setThumbnailUrl(imageUrl);
        courseRepository.save(course);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedCreateAt = crashCourseVideo.getCreateAt().format(formatter);

        return new CrashCourseResponseDTO(crashCourseVideo.getId(),crashCourseVideo.getLink(), signedImageUrl,crashCourseVideo.getDuration(), formattedCreateAt);
    }

    public UpdateCrashCourseDTO updateCrashCourseVideo(CrashCourseRequestDTO crashCourseRequestDTO, Integer id, String url, Account account) {

        if (crashCourseRequestDTO.getMediaDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer");
        }

        Media crashCourseVideo = crashCourseVideoRepository.findCrashCourseVideoById(id);
        if (crashCourseVideo == null) {
            throw new IllegalArgumentException("Media not found with id: " + id);
        }
        int maxWidthSizeImage = 1000;
        String signedImageUrl = null;
        String imageUrl = null;
        String folderName = account.getEmail() + url;
        if (crashCourseRequestDTO.getMediaThumnail() == null || crashCourseRequestDTO.getMediaThumnail().isEmpty()) {
            imageUrl = null;
        } else {
            try {
                imageUrl = uploadFileUtils.uploadFile(folderName, crashCourseRequestDTO.getMediaThumnail(), maxWidthSizeImage);
                signedImageUrl = uploadFileUtils.getSignedImageUrl(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        crashCourseVideo.setLink(crashCourseRequestDTO.getMediaLink());
        crashCourseVideo.setThumbnail(imageUrl);
        crashCourseVideo.setDuration(crashCourseRequestDTO.getMediaDuration());
        LocalDateTime dateTime = LocalDateTime.now();
        crashCourseVideo.setUpdateAt(dateTime);  // Update the updateAt field with the current time
        crashCourseVideoRepository.save(crashCourseVideo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedUpdateAt = crashCourseVideo.getUpdateAt().format(formatter); // Format the updateAt field

        return new UpdateCrashCourseDTO(crashCourseVideo.getLink(), crashCourseVideo.getDuration(), signedImageUrl,formattedUpdateAt);
    }

    public DeleteCrashCourseDTO deleteCrashCourseVideo(Integer id) {

            Media crashCourseVideo = crashCourseVideoRepository.findCrashCourseVideoById(id);
            if (crashCourseVideo == null) {
                throw new IllegalArgumentException("Media not found with id: " + id);
            }

            crashCourseVideo.setIsDeleted(true);
            crashCourseVideoRepository.save(crashCourseVideo);
            return new DeleteCrashCourseDTO("Delete Successful!!", crashCourseVideo.getIsDeleted());

    }
}
