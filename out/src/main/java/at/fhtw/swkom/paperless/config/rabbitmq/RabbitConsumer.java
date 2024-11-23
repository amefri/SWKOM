package at.fhtw.swkom.paperless.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class RabbitConsumer {
    private static final String QUEUE_NAME = "ocr_queue";

    public RabbitConsumer() {
        try {
            // Set up RabbitMQ connection
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost"); // Replace with your RabbitMQ host
            factory.setUsername("user"); // Replace with RabbitMQ username
            factory.setPassword("password"); // Replace with RabbitMQ password

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // Declare the queue
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println("Waiting for messages from " + QUEUE_NAME + "...");

            // Define the callback for message processing
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("Received message: " + message);

                try {
                    // Deserialize the message
                    ObjectMapper objectMapper = new ObjectMapper();
                    RabbitMessage rabbitMessage = objectMapper.readValue(message, RabbitMessage.class);

                    // Process the file content (if any)
                    if (rabbitMessage.getFileContent() != null) {
                        byte[] fileBytes = Base64.getDecoder().decode(rabbitMessage.getFileContent());
                        String outputPath = "output/" + rabbitMessage.getFileName(); // Save file in "output/" directory
                        Files.createDirectories(Paths.get("output")); // Ensure directory exists
                        Files.write(Paths.get(outputPath), fileBytes);
                        System.out.println("File saved to: " + outputPath);
                    }

                    // Process additional metadata
                    System.out.println("Title: " + rabbitMessage.getTitle());
                    System.out.println("File Name: " + rabbitMessage.getFileName());
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                    e.printStackTrace();
                }
            };

            // Start consuming messages
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class for RabbitMQ message format
    public static class RabbitMessage {
        private String title;
        private String fileName;
        private String fileContent; // Base64-encoded content

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }
    }
}
