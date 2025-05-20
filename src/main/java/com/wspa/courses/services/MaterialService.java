package com.wspa.courses.services;

import com.wspa.courses.entities.Course;
import com.wspa.courses.entities.Material;
import com.wspa.courses.repos.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository repo;

    public MaterialService(MaterialRepository repo) {
        this.repo = repo;
    }

    public List<Material> forCourse(Long c) {
        return repo.findAllByCourseId(c);
    }
}