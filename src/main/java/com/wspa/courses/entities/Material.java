package com.wspa.courses.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "static/materials")
@Data
@NoArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* --- FK to course --- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** e.g. "PDF", "VIDEO", "LINK" */
    private String materialType;

    private String fileUrl;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}