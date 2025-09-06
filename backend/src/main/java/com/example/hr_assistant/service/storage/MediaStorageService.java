package com.example.hr_assistant.service.storage;

import com.example.hr_assistant.config.MinioConfig;
import com.example.hr_assistant.model.dto.MediaUploadResponse;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для работы с медиа хранилищем (MinIO/S3)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * Инициализирует bucket если не существует
     */
    public void initializeBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .build()
                );
                log.info("Bucket {} создан", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            log.error("Ошибка при инициализации bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка инициализации хранилища", e);
        }
    }

    /**
     * Загружает файл в хранилище
     */
    public MediaUploadResponse uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = generateFileName(file.getOriginalFilename(), folder);
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            String fileUrl = getFileUrl(fileName);
            
            MediaUploadResponse response = new MediaUploadResponse();
            response.setFileUrl(fileUrl);
            response.setFileSize(file.getSize());
            response.setContentType(file.getContentType());
            response.setSuccess(true);
            response.setMessage("Файл успешно загружен");
            
            log.info("Файл загружен: {}", fileName);
            
            return response;
            
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
            
            MediaUploadResponse response = new MediaUploadResponse();
            response.setSuccess(false);
            response.setMessage("Ошибка загрузки файла: " + e.getMessage());
            
            return response;
        }
    }

    /**
     * Загружает файл из byte array
     */
    public MediaUploadResponse uploadFile(byte[] fileData, String fileName, String contentType, String folder) {
        try {
            String fullFileName = generateFileName(fileName, folder);
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fullFileName)
                    .stream(new ByteArrayInputStream(fileData), fileData.length, -1)
                    .contentType(contentType)
                    .build()
            );

            String fileUrl = getFileUrl(fullFileName);
            
            MediaUploadResponse response = new MediaUploadResponse();
            response.setFileUrl(fileUrl);
            response.setFileSize((long) fileData.length);
            response.setContentType(contentType);
            response.setSuccess(true);
            response.setMessage("Файл успешно загружен");
            
            log.info("Файл загружен: {}", fullFileName);
            
            return response;
            
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
            
            MediaUploadResponse response = new MediaUploadResponse();
            response.setSuccess(false);
            response.setMessage("Ошибка загрузки файла: " + e.getMessage());
            
            return response;
        }
    }

    /**
     * Создает presigned URL для загрузки
     */
    public String createPresignedUploadUrl(String fileName, String contentType, String folder) {
        try {
            String fullFileName = generateFileName(fileName, folder);
            
            String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(minioConfig.getBucketName())
                    .object(fullFileName)
                    .expiry(1, TimeUnit.HOURS)
                    .build()
            );
            
            log.info("Presigned URL создан для: {}", fullFileName);
            
            return presignedUrl;
            
        } catch (Exception e) {
            log.error("Ошибка при создании presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка создания presigned URL", e);
        }
    }

    /**
     * Создает presigned URL для скачивания
     */
    public String createPresignedDownloadUrl(String fileName) {
        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .expiry(1, TimeUnit.HOURS)
                    .build()
            );
            
            return presignedUrl;
            
        } catch (Exception e) {
            log.error("Ошибка при создании presigned download URL: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка создания presigned download URL", e);
        }
    }

    /**
     * Получает файл из хранилища
     */
    public InputStream getFile(String fileName) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            log.error("Ошибка при получении файла {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения файла", e);
        }
    }

    /**
     * Удаляет файл из хранилища
     */
    public boolean deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build()
            );
            
            log.info("Файл удален: {}", fileName);
            return true;
            
        } catch (Exception e) {
            log.error("Ошибка при удалении файла {}: {}", fileName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Проверяет существование файла
     */
    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Генерирует уникальное имя файла
     */
    private String generateFileName(String originalFileName, String folder) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(originalFileName);
        String baseName = getBaseFileName(originalFileName);
        
        return String.format("%s/%s_%s%s", folder, baseName, timestamp, extension);
    }

    /**
     * Получает расширение файла
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Получает базовое имя файла без расширения
     */
    private String getBaseFileName(String fileName) {
        if (fileName == null) {
            return "file";
        }
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * Получает полный URL файла
     */
    private String getFileUrl(String fileName) {
        return String.format("%s/%s/%s", minioConfig.getEndpoint(), minioConfig.getBucketName(), fileName);
    }

    /**
     * Получает размер файла
     */
    public long getFileSize(String fileName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build()
            );
            return stat.size();
        } catch (Exception e) {
            log.error("Ошибка при получении размера файла {}: {}", fileName, e.getMessage());
            return 0;
        }
    }
}
