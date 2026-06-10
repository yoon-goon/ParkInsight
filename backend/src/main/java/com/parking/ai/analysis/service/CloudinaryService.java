package com.parking.ai.analysis.service;

import com.cloudinary.Cloudinary;
import com.parking.ai.common.BusinessException;
import com.parking.ai.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file, String tag) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "parkinsight", "tags", tag)
            );
            return result.get("secure_url").toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
