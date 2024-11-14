package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Integer> {
    List<StudentProfile> findByEnrollmentYear(Integer year);
    Optional<StudentProfile> findByNpm(String npm);
    List<StudentProfile> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
} 