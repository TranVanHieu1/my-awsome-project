package com.ojt.mockproject.repository;


import com.ojt.mockproject.entity.Course;
import com.ojt.mockproject.entity.certificate_quiz.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    Certificate findByCourse(Course course);

}
