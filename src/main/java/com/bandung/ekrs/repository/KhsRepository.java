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
            JOIN e.student s
            JOIN e.semester sem
            JOIN s.account a
            WHERE sem.id = :semester
            AND a.username = :username
            ORDER BY e.course.courseCode
            """)
    List<Grade> findBySemesterAndUsername(@Param("semester") Integer semester, 
                                        @Param("username") String username);
}
