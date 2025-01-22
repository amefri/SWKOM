package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.config.rabbitmq.RabbitMQProducer;
import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.persistence.entity.RabbitMessage;
import at.fhtw.swkom.paperless.services.DocumentService;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mapper.DocumentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaperlessApiControllerTest {

    @Mock
    private DocumentService documentService;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @InjectMocks
    private PaperlessApiController paperlessApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests if a document can be retrieved successfully when it exists.
    // Validates the interaction between the controller, service, and mapper layers for a successful case.
    @Test
    void testGetDocument_Success() {
        Integer documentId = 1;
        Document mockDocument = new Document();
        mockDocument.setId(documentId);
        mockDocument.setTitle("Test Document");

        DocumentDTO mockDocumentDTO = new DocumentDTO();
        mockDocumentDTO.setTitle("Test Document");

        when(documentService.getDocumentById(documentId)).thenReturn(Optional.of(mockDocument));
        when(documentMapper.toDto(mockDocument)).thenReturn(mockDocumentDTO);

        ResponseEntity<DocumentDTO> response = paperlessApiController.getDocument(documentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Document", response.getBody().getTitle());
        verify(documentService, times(1)).getDocumentById(documentId);
        verify(documentMapper, times(1)).toDto(mockDocument);
    }

    //Tests the behavior when trying to retrieve a non-existent document.
    //Ensures proper HTTP 404 response is returned when the requested document is not found.
    @Test
    void testGetDocument_NotFound() {
        Integer documentId = 1;

        when(documentService.getDocumentById(documentId)).thenReturn(Optional.empty());

        ResponseEntity<DocumentDTO> response = paperlessApiController.getDocument(documentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(documentService, times(1)).getDocumentById(documentId);
    }


    //Tests deleting a document when it exists.
    //Ensures the document is properly deleted and returns an appropriate HTTP 204 response.
    @Test
    void testDeleteDocument_Success() {
        Integer documentId = 1;

        when(documentService.getDocumentById(documentId)).thenReturn(Optional.of(new Document()));

        ResponseEntity<Void> response = paperlessApiController.deleteDocument(documentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(documentService, times(1)).getDocumentById(documentId);
        verify(documentService, times(1)).deleteDocumentById(documentId);
    }

    //Tests attempting to delete a non-existent document.
    //Validates error handling and ensures an HTTP 404 response is returned for invalid delete requests.
    @Test
    void testDeleteDocument_NotFound() {
        Integer documentId = 1;

        when(documentService.getDocumentById(documentId)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = paperlessApiController.deleteDocument(documentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(documentService, times(1)).getDocumentById(documentId);
    }

    //Tests fetching a list of all documents and maps them to DTOs.
    //Verifies the retrieval of multiple documents and confirms the mapper processes them correctly for output.
    @Test
    void testGetDocuments_Success() {
        Document mockDocument1 = new Document();
        mockDocument1.setId(1);
        mockDocument1.setTitle("Document 1");

        Document mockDocument2 = new Document();
        mockDocument2.setId(2);
        mockDocument2.setTitle("Document 2");

        DocumentDTO mockDocumentDTO1 = new DocumentDTO();
        mockDocumentDTO1.setTitle("Document 1");

        DocumentDTO mockDocumentDTO2 = new DocumentDTO();
        mockDocumentDTO2.setTitle("Document 2");

        when(documentService.getAllDocuments()).thenReturn(Arrays.asList(mockDocument1, mockDocument2));
        when(documentMapper.toDto(mockDocument1)).thenReturn(mockDocumentDTO1);
        when(documentMapper.toDto(mockDocument2)).thenReturn(mockDocumentDTO2);

        ResponseEntity<List<DocumentDTO>> response = paperlessApiController.getDocuments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(documentService, times(1)).getAllDocuments();
        verify(documentMapper, times(1)).toDto(mockDocument1);
        verify(documentMapper, times(1)).toDto(mockDocument2);
    }
}