package at.fhtw.swkom.paperless.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Log
@Service
public class MinioService {
//handles file storage and retrieval

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(String fileName, InputStream inputStream, String contentType) throws Exception {
        final int tenMB = 10 * 1024 * 1024;
        final String minioFilename = UUID.randomUUID() + "-" + fileName;
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(minioFilename)
                    .stream(inputStream, -1, tenMB)
                    .contentType(contentType)
                    .build());
            log.info("File uploaded successfully: " + fileName);
        } catch (MinioException e) {
            log.info("Error occurred while uploading file to MinIO: " + e.getMessage());
            throw e;
        }
        return minioFilename;
    }

    public InputStream downloadFile(String fileName) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (MinioException e) {
            System.err.println("Error occurred while downloading file from MinIO: " + e.getMessage());
            throw e;
        }
    }
}
