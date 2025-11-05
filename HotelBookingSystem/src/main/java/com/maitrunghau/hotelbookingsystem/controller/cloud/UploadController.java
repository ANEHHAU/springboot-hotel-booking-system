package com.maitrunghau.hotelbookingsystem.controller.cloud;

import com.maitrunghau.hotelbookingsystem.response.ApiResponse;
import com.maitrunghau.hotelbookingsystem.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    /**
     * 🟩 Upload avatar hoặc bất kỳ hình ảnh nào lên Cloudinary.
     * @param file ảnh được gửi từ client (form-data)
     */
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<Map>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            log.info("⬆️ Bắt đầu upload avatar: {}", file.getOriginalFilename());
            Map result = cloudinaryService.uploadFile(file, "customer_avatars");

            return ResponseEntity.ok(
                    ApiResponse.success("Upload avatar thành công", result)
            );

        } catch (IOException e) {
            log.error("❌ Lỗi khi upload avatar: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Upload avatar thất bại"));
        }
    }

    /**
     * 🟥 Xóa file Cloudinary theo public_id
     */
    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String publicId) {
        try {
            log.warn("🗑️ Xóa file có public_id: {}", publicId);
            cloudinaryService.deleteFile(publicId);
            return ResponseEntity.ok(ApiResponse.success("Đã xóa ảnh thành công", null));
        } catch (IOException e) {
            log.error("❌ Lỗi khi xóa ảnh: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa ảnh"));
        }
    }
}
