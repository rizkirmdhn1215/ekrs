package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    // Find student by ID
    Optional<Account> findById(Integer id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.password = :password WHERE a.id = :id")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
} 