package com.wspa.courses.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id","student_id"}))
@Data @NoArgsConstructor
public class Enrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)          // FK → course
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)          // FK → user
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    /** 0-100 % */
    private Integer progress = 0;

    private LocalDateTime enrolledAt = LocalDateTime.now();
}