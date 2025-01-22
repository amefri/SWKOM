package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.repository.DocumentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

import static org.mockito.Mockito.*;

class RabbitMQServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveFilename_success() throws Exception {
        // Test for successfully receiving and processing a message from the OCR worker
        String content = "{\"content\":\"Extracted text\",\"minioFilename\":\"testFile.pdf\"}";
        Channel channel = mock(Channel.class);
        RabbitMQService.OCRMessage ocrMessage = new RabbitMQService.OCRMessage("Extracted text", "testFile.pdf");

        when(objectMapper.readValue(content, RabbitMQService.OCRMessage.class)).thenReturn(ocrMessage);

        rabbitMQService.receiveFilename(content, channel, 1L);

        verify(objectMapper, times(1)).readValue(content, RabbitMQService.OCRMessage.class);
        verify(channel, times(1)).basicAck(1L, false);
    }

    @Test
    void testReceiveFilename_jsonProcessingException() throws Exception {
        // Test for handling a JsonProcessingException
        String content = "invalid-json-content";
        Channel channel = mock(Channel.class);

        when(objectMapper.readValue(content, RabbitMQService.OCRMessage.class)).thenThrow(JsonProcessingException.class);

        rabbitMQService.receiveFilename(content, channel, 1L);

        verify(objectMapper, times(1)).readValue(content, RabbitMQService.OCRMessage.class);
        verify(channel, times(1)).basicNack(1L, false, false);
    }

    @Test
    void testReceiveFilename_unexpectedException() throws Exception {
        // Test for handling an unexpected exception
        String content = "{\"content\":\"Extracted text\",\"minioFilename\":\"testFile.pdf\"}";
        Channel channel = mock(Channel.class);

        when(objectMapper.readValue(content, RabbitMQService.OCRMessage.class)).thenThrow(new RuntimeException("Unexpected error"));

        rabbitMQService.receiveFilename(content, channel, 1L);

        verify(objectMapper, times(1)).readValue(content, RabbitMQService.OCRMessage.class);
        verify(channel, times(1)).basicNack(1L, false, true);
    }
}
