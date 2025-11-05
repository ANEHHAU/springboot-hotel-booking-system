package com.maitrunghau.hotelbookingsystem.repository;

import com.maitrunghau.hotelbookingsystem.model.Employee;
import com.maitrunghau.hotelbookingsystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 🔹 Tìm nhân viên theo email
    Optional<Employee> findByEmail(String email);

    // 🔹 Tìm nhân viên theo số điện thoại (dùng @Query để tránh lỗi mapping)
    @Query("SELECT e FROM Employee e WHERE e.phone_number = :phone")
    Optional<Employee> findByPhoneNumber(@Param("phone") String phone_number);

    // 🔹 Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // 🔹 Kiểm tra số điện thoại đã tồn tại (dùng @Query cho chắc chắn)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.phone_number = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phone_number);

    // 🔹 Đếm số lượng nhân viên theo vai trò (để kiểm tra có Admin chưa)
    long countByRole(Role role);

    // 🔹 Lấy danh sách nhân viên đang hoạt động
    @Query("SELECT e FROM Employee e WHERE e.active = true")
    List<Employee> findActiveEmployees();

    // 🔹 Tìm nhân viên theo tên (bỏ qua hoa/thường)
    @Query("SELECT e FROM Employee e WHERE LOWER(e.full_name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> searchByName(@Param("name") String name);
}
