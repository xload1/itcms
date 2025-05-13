package com.wspa.courses.services;

import com.wspa.courses.entities.Course;
import com.wspa.courses.repos.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository repo;

    public CourseService(CourseRepository repo) { this.repo = repo; }

    public List<Course> getFiltered(Optional<String> level, Optional<String> durationCat) {

        List<Course> base = level.map(repo::findByLevel).orElseGet(repo::findAll);

        return durationCat.map(dc -> base.stream()
                        .filter(c -> switch (dc) {
                            case "lt1"   -> c.getDurationMonths() < 1;
                            case "1to3"  -> c.getDurationMonths() >= 1 && c.getDurationMonths() <= 3;
                            case "gt3"   -> c.getDurationMonths() > 3;
                            default      -> true;
                        })
                        .collect(Collectors.toList()))
                .orElse(base);
    }
}