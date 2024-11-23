package at.fhtw.swkom.paperless.config.rabbitmq;

import org.springframework.stereotype.Component;
import com.rabbitmq.client.*;

@Component
public class RabbitConsumer {
    private static final String QUEUE_NAME = "ocr_queue";

    public RabbitConsumer() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost"); // Replace with your RabbitMQ host
            factory.setUsername("user");
            factory.setPassword("password");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println("Waiting for messages from " + QUEUE_NAME + "...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("Received message: " + message);

                // Process the message here
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
