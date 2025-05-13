package com.wspa.courses.services;

import com.wspa.courses.entities.Course;
import com.wspa.courses.entities.Enrollment;
import com.wspa.courses.entities.Users;
import com.wspa.courses.repos.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {
    private final EnrollmentRepository repo;

    public EnrollmentService(EnrollmentRepository repo) { this.repo = repo; }

    public List<Enrollment> forUser(Users u) { return repo.findByStudent(u); }

    public Enrollment enrollIfAbsent(Users user, Course course) {
        return repo.findByStudentAndCourseId(user, course.getId())
                .orElseGet(() -> {
                    Enrollment e = new Enrollment();
                    e.setStudent(user);
                    e.setCourse(course);
                    return repo.save(e);
                });
    }
}