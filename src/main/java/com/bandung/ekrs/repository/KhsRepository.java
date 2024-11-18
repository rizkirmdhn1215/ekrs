package com.bandung.ekrs.repository;


import com.bandung.ekrs.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KhsRepository extends JpaRepository<Grade, Integer> {
    @Query("""
            SELECT g FROM Grade g
            JOIN g.enrollment e
            JOIN e.course c
            JOIN e.semester s
            WHERE s.name = :semester
            AND e.student.account.username = :username
            """)
    List<Grade> findBySemesterAndUsername(@Param("semester") String semester, 
                                        @Param("username") String username);
}
