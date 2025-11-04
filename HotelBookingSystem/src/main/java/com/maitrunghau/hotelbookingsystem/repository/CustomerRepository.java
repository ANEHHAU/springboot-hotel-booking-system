package com.maitrunghau.hotelbookingsystem.repository;

import com.maitrunghau.hotelbookingsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    @Query("SELECT c FROM Customer c WHERE c.phone_number = :phone")
    Optional<Customer> findByPhoneNumber(@Param("phone") String phone_number);

    boolean existsByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.phone_number = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phone_number);
}
// dùng query vì lỗi tên hàm