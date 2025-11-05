package com.maitrunghau.hotelbookingsystem.common;

import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.model.Employee;
import com.maitrunghau.hotelbookingsystem.repository.CustomerRepository;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 Đang xác thực người dùng: {}", username);

        // ✅ Ưu tiên tìm trong Employee (email hoặc phone)
        Employee employee = employeeRepository.findByEmail(username)
                .or(() -> employeeRepository.findByPhoneNumber(username))
                .orElse(null);

        if (employee != null) {
            if (!employee.getActive()) throw new UsernameNotFoundException("Tài khoản nhân viên đã bị vô hiệu hóa!");
            return new User(
                    employee.getEmail(),
                    employee.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()))
            );
        }

        // ✅ Sau đó tìm trong Customer (email hoặc phone)
        Customer customer = customerRepository.findByEmail(username)
                .or(() -> customerRepository.findByPhoneNumber(username))
                .orElse(null);

        if (customer != null) {
            if (!customer.getActive()) throw new UsernameNotFoundException("Tài khoản khách hàng đã bị vô hiệu hóa!");
            return new User(
                    customer.getEmail(),
                    customer.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()))
            );
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng có email/số điện thoại: " + username);
    }
}
