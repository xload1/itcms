package com.wspa.courses.repos;

import com.wspa.courses.entities.Enrollment;
import com.wspa.courses.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Users student);
    Optional<Enrollment> findByStudentAndCourseId(Users student, Long courseId);
}