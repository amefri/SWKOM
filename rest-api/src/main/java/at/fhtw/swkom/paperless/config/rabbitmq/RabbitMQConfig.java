package at.fhtw.swkom.paperless.config.rabbitmq;


import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "";

    @Value("${rabbitmq.toOcrWorker}")
    private String ocrWorkerOutputQueueName;
    @Value("${rabbitmq.fromOcrWorker}")
    private String ocrWorkerInputQueueNAME;

    public static final String ECHO_MESSAGE_COUNT_PROPERTY_NAME = "MessageCount";

    @Bean
    public Queue ocrWorkerOutputQueue() {
        return new Queue(ocrWorkerOutputQueueName, true); // Durable queue
    }

    @Bean
    public Queue ocrWorkerInputQueue() {
        return new Queue(ocrWorkerInputQueueNAME, true); // Durable queue
    }
}
