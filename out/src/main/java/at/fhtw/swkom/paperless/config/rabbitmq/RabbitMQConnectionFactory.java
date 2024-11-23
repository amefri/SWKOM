package at.fhtw.swkom.paperless.config.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnectionFactory {

    private static Connection connection;

    public static Connection getConnection() throws Exception {
        if (connection == null || !connection.isOpen()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(System.getenv("RABBITMQ_HOST"));  // Host: "rabbitmq" from docker-compose
            factory.setPort(Integer.parseInt(System.getenv("RABBITMQ_PORT"))); // Port: 5672
            factory.setUsername(System.getenv("RABBITMQ_USER"));  // Username: "user"
            factory.setPassword(System.getenv("RABBITMQ_PASSWORD")); // Password: "password"
            connection = factory.newConnection();
        }
        return connection;
    }
}
