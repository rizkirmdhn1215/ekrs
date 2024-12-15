package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Material;
import com.bandung.ekrs.model.enums.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    
    @Query("SELECT m FROM Material m " +
           "WHERE m.course.id = :courseId " +
           "AND m.isDeleted = false " +
           "ORDER BY m.uploadDate DESC")
    List<Material> findByCourseIdAndNotDeleted(@Param("courseId") Integer courseId);
    
    @Query("SELECT m FROM Material m " +
           "WHERE m.course.id = :courseId " +
           "AND m.fileType = :fileType " +
           "AND m.isDeleted = false " +
           "ORDER BY m.uploadDate DESC")
    List<Material> findByCourseIdAndFileType(
        @Param("courseId") Integer courseId,
        @Param("fileType") MaterialType fileType
    );
} 