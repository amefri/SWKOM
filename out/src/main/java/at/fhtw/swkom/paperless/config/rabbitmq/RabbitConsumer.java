package at.fhtw.swkom.paperless.config.rabbitmq;

import at.fhtw.swkom.paperless.persistence.entity.RabbitMessage;
import at.fhtw.swkom.paperless.services.WorkerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    private final WorkerService workerService;

    public RabbitConsumer(WorkerService workerService) {
        this.workerService = workerService;
    }

    @RabbitListener(queues = "ocr_queue")
    public void listen(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RabbitMessage rabbitMessage = objectMapper.readValue(message, RabbitMessage.class);

            String filePath = "documents/" + rabbitMessage.getFileName();
            System.out.println("Processing file from path: " + filePath);
            String language = "eng+deu"; // OCR language
            String title = rabbitMessage.getTitle();

            // Determine file extension
            String fileExtension = getFileExtension(rabbitMessage.getFileName());

            // Process the document using WorkerService
            workerService.processAndUploadDocument(filePath, language, title, fileExtension);

        } catch (Exception e) {
            System.err.println("Error processing RabbitMQ message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex).toLowerCase();
        }
        return ""; // Return empty string if no extension is present
    }
}
