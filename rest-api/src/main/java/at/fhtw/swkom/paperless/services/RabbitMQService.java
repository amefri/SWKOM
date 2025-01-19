package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.repository.DocumentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Log
@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.toOcrWorker}")
    private String routingKey;
    private final ObjectMapper objectMapper;
    private final DocumentRepository documentRepository;

    public RabbitMQService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, DocumentRepository documentRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.documentRepository = documentRepository;
    }

    public void sendToOCRWorker(String filename) {
        rabbitTemplate.convertAndSend(routingKey, filename);
        log.info("Sent "+filename+" to OCR worker");
    }



    @RabbitListener(queues = "${rabbitmq.fromOcrWorker}")
    public void receiveFilename(String content, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        final OCRMessage ocrMessage;
        try {
            // Attempt to parse the message
            ocrMessage = objectMapper.readValue(content, OCRMessage.class);
            log.info("OCR message received: " + ocrMessage);

            // Acknowledge the message on success
            channel.basicAck(tag, false);
            //int result = documentRepository.updateContentByContentNullAndMinioFileNameLike(ocrMessage.content(), ocrMessage.minioFilename());
            //log.info("Result of Update of content: " + result);
        } catch (JsonProcessingException e) {
            log.severe("Failed to parse OCR message. Content: " + content + " " + Arrays.toString(e.getStackTrace()));

            // Move the unparseable message to the dead-letter queue
            try {
                channel.basicNack(tag, false, false); // Do not requeue the message
            } catch (IOException ioException) {
                log.severe("Failed to nack message: " + ioException.getMessage() + " " + ioException);
            }
        } catch (Exception e) {
            log.severe("Unexpected error occurred " + e);

            // Requeue the message for retry in case of other errors
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ioException) {
                log.severe("Failed to nack message: " + ioException.getMessage() + " " + ioException);
            }
        }
    }

    record OCRMessage(String content, String minioFilename) {}
}
