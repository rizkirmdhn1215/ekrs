package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Integer> {
    Optional<AdminProfile> findByEmail(String email);
    List<AdminProfile> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
} 