package com.wspa.courses.repos;

import com.wspa.courses.entities.Material;
import com.wspa.courses.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findAllByCourseId(Long courseId);
}
