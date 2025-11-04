package com.maitrunghau.hotelbookingsystem.service.impl;

import com.maitrunghau.hotelbookingsystem.config.cloudinary.CloudinaryProperties;
import com.maitrunghau.hotelbookingsystem.dto.CustomerDTO;
import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.model.Role;
import com.maitrunghau.hotelbookingsystem.repository.CustomerRepository;
import com.maitrunghau.hotelbookingsystem.service.CloudinaryService;
import com.maitrunghau.hotelbookingsystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CloudinaryService cloudinaryService;
    private final CloudinaryProperties cloudinaryProperties;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    public String getAvatar() {
        return cloudinaryProperties.getDefaults().getAvatarCustomer();
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer addCustomer(CustomerDTO dto, MultipartFile avatarFile) {
        log.info("🟢 Bắt đầu thêm khách hàng mới: {}", dto.getEmail());

        String avatarUrl  = cloudinaryProperties.getDefaults().getAvatarCustomer();

        try {

            if (avatarFile != null && !avatarFile.isEmpty()) {
                Map<String, Object> uploadResult = cloudinaryService.uploadFile(avatarFile, "avatars/customers");
                if (uploadResult != null && uploadResult.get("secure_url") != null) {
                    avatarUrl = uploadResult.get("secure_url").toString();
                    log.info("✅ Upload avatar thành công cho {}: {}", dto.getEmail(), avatarUrl);
                } else {
                    log.warn("⚠️ Upload avatar thất bại cho {}, dùng ảnh mặc định", dto.getEmail());
                }
            }

            Customer customer = Customer.builder()
                    .full_name(dto.getFull_name())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role(Role.Customer)
                    .phone_number(dto.getPhone_number())
                    .address(dto.getAddress())
                    .date_of_birth(dto.getDate_of_birth())
                    .avatar(avatarUrl)
                    .active(dto.getActive() != null ? dto.getActive() : true)
                    .build();

            Customer saved = customerRepository.save(customer);
            log.info("💾 Đã lưu khách hàng mới: ID={}, Email={}", saved.getId(), saved.getEmail());
            return saved;

        } catch (IOException e) {
            log.error("❌ Lỗi khi upload avatar cho khách hàng {}: {}", dto.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Không thể upload avatar cho khách hàng");
        }
    }

    @Override
    public Customer updateCustomer(Long id, CustomerDTO dto, MultipartFile avatarFile) {
        log.info("🟡 Cập nhật thông tin khách hàng ID={}", id);

        return customerRepository.findById(id).map(existing -> {
            try {
                // ✅ Upload avatar mới nếu có
                if (avatarFile != null && !avatarFile.isEmpty()) {
                    Map<String, Object> uploadResult = cloudinaryService.uploadFile(avatarFile, "avatars/customers");
                    if (uploadResult != null && uploadResult.get("secure_url") != null) {
                        String newUrl = uploadResult.get("secure_url").toString();
                        existing.setAvatar(newUrl);
                        log.info("🖼️ Cập nhật avatar mới cho ID {}: {}", id, newUrl);
                    } else {
                        log.warn("⚠️ Upload avatar mới thất bại cho ID {}", id);
                    }
                }

                // ✅ Cập nhật các trường khác
                existing.setFull_name(dto.getFull_name());
                existing.setEmail(dto.getEmail());
                existing.setPhone_number(dto.getPhone_number());
                existing.setAddress(dto.getAddress());
                existing.setDate_of_birth(dto.getDate_of_birth());
                existing.setRole(Role.Customer);
                existing.setActive(dto.getActive() != null ? dto.getActive() : existing.getActive());

                // ✅ Nếu có mật khẩu mới thì mã hóa lại, ngược lại giữ nguyên
                if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                    String encodedPassword = passwordEncoder.encode(dto.getPassword());
                    existing.setPassword(encodedPassword);
                    log.info("🔐 Đã thay đổi mật khẩu cho khách hàng ID={}", id);
                } else {
                    log.debug("🔒 Không nhập mật khẩu mới, giữ nguyên mật khẩu cũ cho ID={}", id);
                }

                Customer updated = customerRepository.save(existing);
                log.info("✅ Cập nhật thành công khách hàng ID={}", id);
                return updated;

            } catch (IOException e) {
                log.error("❌ Lỗi khi upload avatar mới cho khách hàng {}: {}", id, e.getMessage(), e);
                throw new RuntimeException("Không thể upload avatar mới");
            }

        }).orElseThrow(() -> {
            log.warn("⚠️ Không tìm thấy khách hàng có ID: {}", id);
            return new RuntimeException("Không tìm thấy khách hàng có ID: " + id);
        });
    }



    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách hàng có ID: " + id);
        }
        customerRepository.deleteById(id);
    }
}
