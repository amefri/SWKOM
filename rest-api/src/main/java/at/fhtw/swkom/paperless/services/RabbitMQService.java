package at.fhtw.swkom.paperless.services;

import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log
@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.toOcrWorker}")
    private String routingKey;

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToOCRWorker(String filename) {
        rabbitTemplate.convertAndSend(routingKey, filename);
        log.info("Sent "+filename+" to OCR worker");
    }

}
