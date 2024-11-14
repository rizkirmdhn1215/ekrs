package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    Optional<Semester> findByName(String name);
    List<Semester> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<Semester> findByStartDateBeforeAndEndDateAfter(LocalDate date, LocalDate sameDate);
} 