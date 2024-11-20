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
            WHERE a.username = :username
            AND e.finished = true
            AND sem.id = :semesterId
            ORDER BY sem.id, e.course.courseCode
            """)
    List<Grade> findAllByUsernameAndSemesterId(@Param("username") String username, @Param("semesterId") Integer semesterId);
}
