package com.ojt.mockproject.service;

import com.ojt.mockproject.dto.Certificate.CertificateResponseDTO;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.certificate_quiz.Certificate;
import com.ojt.mockproject.exceptionhandler.ErrorCode;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.repository.CertificateRepository;
import com.ojt.mockproject.repository.CourseRepository;
import com.ojt.mockproject.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CertificateService {
    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    public void copyCertificateDetails(Certificate oldCertificate, Certificate newCertificate, Course newCourse) {
        newCertificate.setCourse(newCourse);
        newCertificate.setName(oldCertificate.getName());
        newCertificate.setDescription(oldCertificate.getDescription());
        newCertificate.setCreateAt(LocalDateTime.now());
        newCertificate.set_Deleted(false);
    }

    public CertificateResponseDTO getCertificateByCourse(Integer courseId) {
        Course course = courseRepository.findCourseById(courseId);

        if (course == null) {
            throw new CourseException("Course not found for ID: " + courseId, ErrorCode.COURSE_NOT_FOUND);
        }

        Certificate certificate = certificateRepository.findByCourse(course);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedCreateAt = certificate.getCreateAt().format(formatter);

        Account currentAccount = accountUtils.getCurrentAccount();
        return new CertificateResponseDTO(certificate.getId(), certificate.getName(), currentAccount.getName(),certificate.getDescription(), formattedCreateAt);
    }


}
