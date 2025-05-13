package com.wspa.courses.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data @NoArgsConstructor
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Beginner / Intermediate / Advanced  */
    private String level;

    /** How many months the course lasts (for filtering) */
    private Integer durationMonths;

    /* FK → instructor */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Users instructor;

    private LocalDateTime createdAt = LocalDateTime.now();
}