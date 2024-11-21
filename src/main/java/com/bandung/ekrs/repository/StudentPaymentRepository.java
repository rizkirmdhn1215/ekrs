package com.bandung.ekrs.repository;

import com.bandung.ekrs.dto.PaymentSummaryDTO;
import com.bandung.ekrs.model.StudentPayment;
import com.bandung.ekrs.model.StudentProfile;
import com.bandung.ekrs.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Query("SELECT SUM(sp.amount) FROM StudentPayment sp WHERE sp.student = :student AND sp.paymentStatus = 'Paid'")
    BigDecimal getTotalPaidAmount(@Param("student") StudentProfile student);
    
    @Query("SELECT sp FROM StudentPayment sp WHERE sp.student = :student ORDER BY sp.paymentDate DESC")
    List<StudentPayment> findLatestPayments(@Param("student") StudentProfile student);

    @Query("SELECT sp FROM StudentPayment sp " +
           "WHERE sp.student = :student " +
           "AND (:status IS NULL OR sp.paymentStatus = :status) " +
           "ORDER BY sp.paymentDate DESC")
    Page<StudentPayment> findByStudentAndStatusWithPagination(
            @Param("student") StudentProfile student,
            @Param("status") PaymentStatus status,
            Pageable pageable);

    @Query("SELECT new com.bandung.ekrs.dto.PaymentSummaryDTO(" +
           "COUNT(sp), SUM(sp.amount), sp.paymentStatus) " +
           "FROM StudentPayment sp " +
           "WHERE sp.student = :student " +
           "AND sp.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY sp.paymentStatus")
    List<PaymentSummaryDTO> getPaymentSummary(
            @Param("student") StudentProfile student,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sp FROM StudentPayment sp " +
           "WHERE sp.paymentDate BETWEEN :startDate AND :endDate " +
           "AND (:status IS NULL OR sp.paymentStatus = :status) " +
           "ORDER BY sp.paymentDate DESC")
    Page<StudentPayment> findPaymentsInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") PaymentStatus status,
            Pageable pageable);
} 