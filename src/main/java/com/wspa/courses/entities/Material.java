package com.wspa.courses.entities;

import com.wspa.courses.entities.Course;
import com.wspa.courses.entities.MaterialType;
import com.wspa.courses.util.YouTubeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
public class Material {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)                // FK → courses.id
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)                      // ← enum instead of String
    @Column(name = "material_type", nullable = false)
    private MaterialType materialType;

    /** Display title (e.g. “Lecture 1 — Intro”) */
    @Column(nullable = false)
    private String title;

    /** URL to pdf / video / web page */
    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Transient
    public String getEmbedUrl() {
        if (materialType == MaterialType.VIDEO) {
            return YouTubeUtil.toEmbed(fileUrl);
        }
        return fileUrl;
    }
}
