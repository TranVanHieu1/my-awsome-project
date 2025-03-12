package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.CrashCourseVideo.DeleteCrashCourseDTO;
import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoRequestDTO;
import com.ojt.mockproject.dto.SmallCourseVideo.SmallVideoResponseDTO;
import com.ojt.mockproject.dto.SmallCourseVideo.UpdateSmallCourseVideoDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.CourseChapter;
import com.ojt.mockproject.entity.Lecture;
import com.ojt.mockproject.repository.CourseChapterRepository;
import com.ojt.mockproject.repository.SmallCourseVideoRepository;
import com.ojt.mockproject.utils.AccountUtils;
import com.ojt.mockproject.utils.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmallCourseVideoService {

    @Autowired
    private CourseChapterRepository courseChapterRepository;

    @Autowired
    private SmallCourseVideoRepository smallCourseVideoRepository;
    @Autowired
    private UploadFileUtils uploadFileUtils;

    public void copySmallCourseVideoDetails(Lecture oldVideo, Lecture newVideo, CourseChapter newChapter) {
        newVideo.setCourseChapter(newChapter);
        newVideo.setName(oldVideo.getName());
        newVideo.setThumbnail(oldVideo.getThumbnail());
        newVideo.setDuration(oldVideo.getDuration());
        newVideo.setDescription(oldVideo.getDescription());
        newVideo.setLink(oldVideo.getLink());
        newVideo.setCreateAt(LocalDateTime.now());
        newVideo.setIsDeleted(false);
    }

    public List<Lecture> getSmallCourseVideosByCourseChapterId(Integer courseChapterId) {
        CourseChapter courseChapter = courseChapterRepository.findById(courseChapterId)
                .orElseThrow(() -> new IllegalArgumentException("Course Chapter not found with id: " + courseChapterId));
        List<Lecture> lectures = smallCourseVideoRepository.findByCourseChapter(courseChapter);
        return lectures.stream()
                .filter(lecture -> !lecture.getIsDeleted())
                .collect(Collectors.toList());
    }


    public Lecture getSmallCourseVideosById(Integer id) {
        return smallCourseVideoRepository.findById(id)
                .filter(lecture -> !lecture.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found with id: " + id));
    }

    @Transactional
    public SmallVideoResponseDTO createSmallCourseVideo(SmallVideoRequestDTO smallVideoRequestDTO, Account account, String url) {
        if (smallVideoRequestDTO.getLectureDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer");
        }

        CourseChapter courseChapter = courseChapterRepository.findById(smallVideoRequestDTO.getCourseChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Course Chapter not found with id: " + smallVideoRequestDTO.getCourseChapterId()));

        int maxWidthSizeImage = 1000;
        String signedImageUrl = null;
        String imageUrl = null;
        String folderName = account.getEmail() + url;
        if (smallVideoRequestDTO.getLectureThumbnail() == null || smallVideoRequestDTO.getLectureThumbnail().isEmpty()) {
            imageUrl = null;
        } else {
            try {
                imageUrl = uploadFileUtils.uploadFile(folderName, smallVideoRequestDTO.getLectureThumbnail(), maxWidthSizeImage);
                signedImageUrl = uploadFileUtils.getSignedImageUrl(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Lecture smallCourseVideo = new Lecture();
        smallCourseVideo.setCourseChapter(courseChapter);
        smallCourseVideo.setName(smallVideoRequestDTO.getLectureTitle());
        smallCourseVideo.setDescription(smallVideoRequestDTO.getLectureDescription());
        smallCourseVideo.setThumbnail(signedImageUrl);
        smallCourseVideo.setLink(smallVideoRequestDTO.getLectureLink());
        smallCourseVideo.setDuration(smallVideoRequestDTO.getLectureDuration());
        smallCourseVideo.setCreateAt(LocalDateTime.now());
        smallCourseVideo.setIsDeleted(false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedCreateAt = smallCourseVideo.getCreateAt().format(formatter);

        smallCourseVideoRepository.save(smallCourseVideo);
        return new SmallVideoResponseDTO(smallCourseVideo.getId(), smallCourseVideo.getName(), smallCourseVideo.getDescription(), smallCourseVideo.getThumbnail(), smallCourseVideo.getLink(), smallCourseVideo.getDuration(), formattedCreateAt);
    }

    @Transactional
    public UpdateSmallCourseVideoDTO updateSmallCourseVideo(SmallVideoRequestDTO smallVideoRequestDTO, Integer id) {
        if (smallVideoRequestDTO.getLectureDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be a positive integer");
        }

        Lecture smallCourseVideo = smallCourseVideoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found with id: " + id));

        smallCourseVideo.setName(smallVideoRequestDTO.getLectureTitle());
        smallCourseVideo.setLink(smallVideoRequestDTO.getLectureLink());
        smallCourseVideo.setDuration(smallVideoRequestDTO.getLectureDuration());

        smallCourseVideoRepository.save(smallCourseVideo);

        return new UpdateSmallCourseVideoDTO(smallCourseVideo.getName(), smallCourseVideo.getDescription(), smallCourseVideo.getThumbnail(), smallCourseVideo.getLink(), smallCourseVideo.getDuration());
    }

    @Transactional
    public DeleteCrashCourseDTO deleteSmallCourseVideo(Integer id) {
        Lecture smallCourseVideo = smallCourseVideoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found with id: " + id));

        smallCourseVideo.setIsDeleted(true);
        smallCourseVideoRepository.save(smallCourseVideo);
        return new DeleteCrashCourseDTO("Delete Successful!!", smallCourseVideo.getIsDeleted());
    }
}
