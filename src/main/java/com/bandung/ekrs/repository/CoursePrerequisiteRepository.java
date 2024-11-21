package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.CoursePrerequisite;
import com.bandung.ekrs.model.CoursePrerequisiteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, CoursePrerequisiteId> {
    List<CoursePrerequisite> findByCourseId(Integer courseId);
    List<CoursePrerequisite> findByPrerequisiteCourseId(Integer prerequisiteCourseId);
    List<CoursePrerequisite> findByConditionType(String conditionType);
} 