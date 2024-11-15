package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Integer> {
    Optional<StudentProfile> findByAccount(Account account);
    Optional<StudentProfile> findByNpm(String npm);
} 