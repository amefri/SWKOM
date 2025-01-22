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

    @RabbitListener(queues = "ocr.toWorker")//TODO: Add queue name
    public void listen(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RabbitMessage rabbitMessage = objectMapper.readValue(message, RabbitMessage.class);

            String filePath = "documents/" + rabbitMessage.getFileName();
            String language = "eng+deu"; // OCR language
            String title = rabbitMessage.getTitle();

            // Process the document using WorkerService
            workerService.processDocument(filePath, language, title);

        } catch (Exception e) {
            System.err.println("Error processing RabbitMQ message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
