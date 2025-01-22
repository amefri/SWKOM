package at.fhtw.swkom.paperless.config.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private static final String QUEUE_NAME = "ocr_queue";

    public void sendMessage(String message) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("rabbitmq"); //matches docker host name
            factory.setUsername("user");
            factory.setPassword("password");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send message to RabbitMQ", e);
        }
    }
}
