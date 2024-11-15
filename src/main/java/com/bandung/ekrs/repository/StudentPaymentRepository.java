package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.StudentPayment;
import com.bandung.ekrs.model.StudentProfile;
import com.bandung.ekrs.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentPaymentRepository extends JpaRepository<StudentPayment, Integer> {
    List<StudentPayment> findByStudent(StudentProfile student);
    List<StudentPayment> findByPaymentStatus(PaymentStatus status);
    List<StudentPayment> findByStudentAndPaymentStatus(StudentProfile student, PaymentStatus status);
    
    @Query("SELECT sp FROM StudentPayment sp WHERE sp.paymentDate BETWEEN :startDate AND :endDate")
    List<StudentPayment> findPaymentsBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT SUM(sp.amount) FROM StudentPayment sp WHERE sp.student = :student AND sp.paymentStatus = 'PAID'")
    BigDecimal getTotalPaidAmount(@Param("student") StudentProfile student);
    
    @Query("SELECT sp FROM StudentPayment sp WHERE sp.student = :student ORDER BY sp.paymentDate DESC")
    List<StudentPayment> findLatestPayments(@Param("student") StudentProfile student);
} 