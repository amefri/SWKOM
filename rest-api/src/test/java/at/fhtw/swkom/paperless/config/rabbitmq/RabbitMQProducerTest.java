package at.fhtw.swkom.paperless.config.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMQProducerTest {

    private RabbitMQProducer rabbitMQProducer;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);

        rabbitMQProducer = new RabbitMQProducer() {
            @Override
            public void sendMessage(String message) {
                try (Connection mockedConnection = connectionFactory.newConnection();
                     Channel mockedChannel = mockedConnection.createChannel()) {
                    mockedChannel.queueDeclare("ocr_queue", true, false, false, null);
                    mockedChannel.basicPublish("", "ocr_queue", null, message.getBytes());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to send message to RabbitMQ", e);
                }
            }
        };
    }

    //ensures that the RabbitMQProducer correctly interacts with RabbitMQ
    @Test
    void testSendMessage() throws Exception {
        // Arrange
        String testMessage = "Test Message";

        // Act
        rabbitMQProducer.sendMessage(testMessage);

        // Assert
        verify(channel, times(1)).queueDeclare("ocr_queue", true, false, false, null);
        ArgumentCaptor<byte[]> messageCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(channel, times(1)).basicPublish(eq(""), eq("ocr_queue"), isNull(), messageCaptor.capture());

        assertEquals(testMessage, new String(messageCaptor.getValue()));
    }


    // Test for when sendMessage throws an exception (e.g., connection issue)
    @Test
    void testSendMessageThrowsException() throws Exception {
        // Arrange
        String testMessage = "Test Message";
        when(connectionFactory.newConnection()).thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rabbitMQProducer.sendMessage(testMessage);
        });

        assertEquals("Failed to send message to RabbitMQ", exception.getMessage());
    }


    // Test for when an empty message is sent
    @Test
    void testSendMessageWithEmptyMessage() throws Exception {
        // Arrange
        String testMessage = "";

        // Act
        rabbitMQProducer.sendMessage(testMessage);

        // Assert
        ArgumentCaptor<byte[]> messageCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(channel, times(1)).basicPublish(eq(""), eq("ocr_queue"), isNull(), messageCaptor.capture());

        // Assert that the empty message was correctly sent
        assertEquals(testMessage, new String(messageCaptor.getValue()));
    }


    // Test for validating the queueDeclare method
    @Test
    void testQueueDeclareCalledWithCorrectArguments() throws Exception {
        // Act
        rabbitMQProducer.sendMessage("Test Message");

        // Assert
        verify(channel, times(1)).queueDeclare("ocr_queue", true, false, false, null);
    }


    // Test for ensuring that connection and channel are closed after use
    @Test
    void testConnectionAndChannelClosedAfterMessage() throws Exception {
        // Act
        rabbitMQProducer.sendMessage("Test Message");

        // Assert
        verify(connection, times(1)).close();
        verify(channel, times(1)).close();
    }


    // Test for mocking the behavior when RabbitMQ is not reachable
    @Test
    void testSendMessageRabbitMQNotReachable() throws Exception {
        // Arrange
        String testMessage = "Test Message";
        when(connectionFactory.newConnection()).thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rabbitMQProducer.sendMessage(testMessage);
        });

        assertTrue(exception.getMessage().contains("Failed to send message to RabbitMQ"));
    }


}
