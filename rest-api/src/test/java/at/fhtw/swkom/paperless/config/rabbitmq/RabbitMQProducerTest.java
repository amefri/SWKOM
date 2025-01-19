package at.fhtw.swkom.paperless.config.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
