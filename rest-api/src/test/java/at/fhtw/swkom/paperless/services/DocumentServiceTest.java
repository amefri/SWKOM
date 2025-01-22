package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.persistence.repository.DocumentRepository;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mapper.DocumentMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private OcrService ocrService;

    @Mock
    private MinioService minioService;

    @Mock
    private RabbitMQService rabbitMQService;

    @Mock
    private DocumentMapper mapper;

    @InjectMocks
    private DocumentService documentService;

    public DocumentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDocumentById() {
        // Test for retrieving a document by ID
        Document document = new Document(1, "Title", "Author", "Content", LocalDateTime.now(), "bucketFileName");
        when(documentRepository.findById(1)).thenReturn(Optional.of(document));

        Optional<Document> result = documentService.getDocumentById(1);

        assertTrue(result.isPresent());
        assertEquals(document, result.get());
        verify(documentRepository, times(1)).findById(1);
    }

    @Test
    void testGetAllDocuments() {
        // Test for retrieving all documents
        List<Document> documents = List.of(new Document(1, "Title1", "Author1", "Content1", LocalDateTime.now(), "bucketFileName1"),
                new Document(2, "Title2", "Author2", "Content2", LocalDateTime.now(), "bucketFileName2"));
        when(documentRepository.findAll()).thenReturn(documents);

        List<Document> result = documentService.getAllDocuments();

        assertEquals(documents.size(), result.size());
        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void testSaveDocument() {
        // Test for saving a document to the repository
        Document document = new Document(1, "Title", "Author", "Content", LocalDateTime.now(), "bucketFileName");

        documentService.saveDocument(document);

        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void testDeleteDocumentById() {
        // Test for deleting a document by ID
        int documentId = 1;

        documentService.deleteDocumentById(documentId);

        verify(documentRepository, times(1)).deleteById(documentId);
    }


    @Test
    void testCreate() throws Exception {
        // Test for creating a document, uploading it to MinIO, and sending a message to RabbitMQ
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "test.pdf";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);
        when(minioService.uploadFile(fileName, inputStream, "application/pdf")).thenReturn("bucketFileName");

        Document document = new Document(null, "DocumentName", "DocumentName", null, LocalDateTime.now(), "bucketFileName");
        when(documentRepository.save(any(Document.class))).thenReturn(document);
        when(documentRepository.findById(any())).thenReturn(Optional.of(document));
        when(mapper.toDto(document)).thenReturn(new DocumentDTO());

        Optional<DocumentDTO> result = documentService.create("DocumentName", file);

        assertTrue(result.isPresent());
        verify(minioService, times(1)).uploadFile(eq(fileName), any(InputStream.class), eq("application/pdf"));
        verify(rabbitMQService, times(1)).sendToOCRWorker("bucketFileName");
        verify(documentRepository, times(1)).save(any(Document.class));
    }
}
