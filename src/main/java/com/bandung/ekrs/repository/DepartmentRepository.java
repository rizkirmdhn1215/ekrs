package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);
    List<Department> findByDepartmentHeadLecturerId(Integer lecturerId);
    List<Department> findByNameContaining(String keyword);
} 