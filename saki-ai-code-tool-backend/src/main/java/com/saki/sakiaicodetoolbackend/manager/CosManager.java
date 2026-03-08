package com.saki.sakiaicodetoolbackend.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.saki.sakiaicodetoolbackend.config.CosClientConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * COS 对象存储管理器
 */
@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到 COS 并返回访问 URL
     *
     * @param key  COS对象键（完整路径）
     * @param file 要上传的文件
     * @return 文件的访问URL，失败返回null
     */
    public String uploadFile(String key, File file) {
        PutObjectResult result = putObject(key, file);
        if (result != null) {
            String url = buildUrl(key);
            log.info("文件上传到 COS 成功：{} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传到 COS 失败：{}，返回结果为空", file.getName());
            return null;
        }
    }

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传图片到 COS
     *
     * @param file 图片文件
     * @return 图片访问 URL，失败返回 null
     * @throws IOException IO异常
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 jpg/png/gif/webp");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            extension = switch (contentType) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/gif" -> ".gif";
                case "image/webp" -> ".webp";
                default -> ".jpg";
            };
        }

        String key = String.format("post-images/%d-%s%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().replace("-", "").substring(0, 8),
                extension);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(contentType);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosClientConfig.getBucket(),
                    key,
                    inputStream,
                    metadata
            );
            PutObjectResult result = cosClient.putObject(putObjectRequest);

            if (result != null) {
                String url = buildUrl(key);
                log.info("图片上传到 COS 成功：{} -> {}", originalFilename, url);
                return url;
            } else {
                log.error("图片上传到 COS 失败：{}，返回结果为空", originalFilename);
                return null;
            }
        }
    }

    /**
     * 构建完整的访问 URL
     *
     * @param key COS对象键
     * @return 完整的访问URL
     */
    private String buildUrl(String key) {
        String host = cosClientConfig.getHost();
        if (host.endsWith("/")) {
            return host + key;
        }
        return host + "/" + key;
    }
}
