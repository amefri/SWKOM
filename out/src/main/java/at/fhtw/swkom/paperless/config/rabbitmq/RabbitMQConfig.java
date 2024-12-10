package at.fhtw.swkom.paperless.config.rabbitmq;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue ocrQueue() {
        return new Queue("ocr_queue", true);
    }
}
