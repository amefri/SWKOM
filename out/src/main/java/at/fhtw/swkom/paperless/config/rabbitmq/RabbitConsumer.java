package at.fhtw.swkom.paperless.config.rabbitmq;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.persistence.entity.RabbitMessage;
import at.fhtw.swkom.paperless.services.DocumentService;
import at.fhtw.swkom.paperless.services.MinioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Component
public class RabbitConsumer {

    private final MinioService minioService;
    private final DocumentService documentService;

    public RabbitConsumer(MinioService minioService, DocumentService documentService) {
        this.minioService = minioService;
        this.documentService = documentService;
    }

    @RabbitListener(queues = "ocr_queue")
    public void listen(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RabbitMessage rabbitMessage = objectMapper.readValue(message, RabbitMessage.class);

            String filePath = "documents/" + rabbitMessage.getFileName();
            byte[] fileBytes = Base64.getDecoder().decode(rabbitMessage.getFileContent());
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                minioService.uploadFile(filePath, inputStream, fileBytes.length, "application/octet-stream");
                System.out.println("File uploaded to MinIO: " + filePath);
            }

            Document document = new Document();
            document.setTitle(rabbitMessage.getTitle());
            documentService.saveDocument(document);
            System.out.println("Metadata saved to PostgreSQL: " + document.getTitle());

        } catch (Exception e) {
            System.err.println("Error processing RabbitMQ message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
