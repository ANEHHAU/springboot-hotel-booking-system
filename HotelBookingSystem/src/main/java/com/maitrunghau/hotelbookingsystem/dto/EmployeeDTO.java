package com.maitrunghau.hotelbookingsystem.dto;

import com.maitrunghau.hotelbookingsystem.model.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String full_name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 đến 255 ký tự")
    private String password;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String phone_number;

    @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự")
    private String address;

    @Past(message = "Ngày sinh phải trước ngày hiện tại")
    private LocalDate date_of_birth;

    @PastOrPresent(message = "Ngày tuyển dụng không hợp lệ")
    private LocalDate hire_date;

    @PositiveOrZero(message = "Lương phải là số dương hoặc 0")
    private Double salary;

    @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
    private String avatar;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean active = true;
}
