package com.maitrunghau.hotelbookingsystem.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.maitrunghau.hotelbookingsystem.config.cloudinary.CloudinaryProperties;
import com.maitrunghau.hotelbookingsystem.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            log.warn("⚠️ Không có file để upload hoặc file trống.");
            return Collections.emptyMap();
        }

        String uploadFolder = (folderName != null && !folderName.isBlank())
                ? folderName
                : (properties.getFolder() != null ? properties.getFolder() : "uploads");

        try {
            log.info("⬆️ Upload file '{}' lên Cloudinary folder: {}", file.getOriginalFilename(), uploadFolder);

            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", uploadFolder,
                            "resource_type", "auto",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            log.info("✅ Upload thành công: public_id={}, secure_url={}",
                    result.get("public_id"), result.get("secure_url"));

            return result;

        } catch (IOException ex) {
            log.error("❌ Lỗi upload file lên Cloudinary: {}", ex.getMessage(), ex);
            throw new RuntimeException("Không thể upload file lên Cloudinary", ex);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            log.warn("⚠️ publicId rỗng, bỏ qua thao tác xóa.");
            return;
        }

        try {
            log.warn("🗑️ Xóa file Cloudinary với public_id: {}", publicId);
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("✅ Kết quả xóa file Cloudinary: {}", result);
        } catch (IOException ex) {
            log.error("❌ Lỗi khi xóa file trên Cloudinary: {}", ex.getMessage(), ex);
            throw new RuntimeException("Không thể xóa file trên Cloudinary", ex);
        }
    }
}
