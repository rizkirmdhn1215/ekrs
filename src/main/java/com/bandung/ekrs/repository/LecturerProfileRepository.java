package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.LecturerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LecturerProfileRepository extends JpaRepository<LecturerProfile, Integer> {
    List<LecturerProfile> findByDepartmentId(Integer departmentId);
    List<LecturerProfile> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
} 