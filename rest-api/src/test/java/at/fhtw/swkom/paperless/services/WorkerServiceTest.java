package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.*;

class WorkerServiceTest {

    @Mock
    private MinioService minioService;

    @Mock
    private OcrService ocrService;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessDocument_success() throws Exception {
        // Test for successfully processing a document
        String filePath = "testFile.pdf";
        String language = "eng";
        String documentTitle = "Test Document";
        String extractedText = "This is the OCR result text.";
        InputStream fileStream = new ByteArrayInputStream("PDF content".getBytes());

        when(minioService.downloadFile(filePath)).thenReturn(fileStream);
        when(ocrService.processDocument(any(), eq(language))).thenReturn(extractedText);

        workerService.processDocument(filePath, language, documentTitle);

        verify(minioService, times(1)).downloadFile(filePath);
        verify(ocrService, times(1)).processDocument(any(), eq(language));
        verify(documentService, times(1)).saveDocument(any(Document.class));
    }

    @Test
    void testProcessDocument_minioFailure() throws Exception {
        // Test for failure when downloading file from MinIO
        String filePath = "testFile.pdf";
        String language = "eng";
        String documentTitle = "Test Document";

        when(minioService.downloadFile(filePath)).thenThrow(new RuntimeException("MinIO download error"));

        workerService.processDocument(filePath, language, documentTitle);

        verify(minioService, times(1)).downloadFile(filePath);
        verifyNoInteractions(ocrService);
        verifyNoInteractions(documentService);
    }

    @Test
    void testProcessDocument_ocrFailure() throws Exception {
        // Test for failure during OCR processing
        String filePath = "testFile.pdf";
        String language = "eng";
        String documentTitle = "Test Document";
        InputStream fileStream = new ByteArrayInputStream("PDF content".getBytes());

        when(minioService.downloadFile(filePath)).thenReturn(fileStream);
        when(ocrService.processDocument(any(), eq(language))).thenThrow(new RuntimeException("OCR processing error"));

        workerService.processDocument(filePath, language, documentTitle);

        verify(minioService, times(1)).downloadFile(filePath);
        verify(ocrService, times(1)).processDocument(any(), eq(language));
        verifyNoInteractions(documentService);
    }

    @Test
    void testProcessDocument_saveDocumentFailure() throws Exception {
        // Test for failure when saving the document to the database
        String filePath = "testFile.pdf";
        String language = "eng";
        String documentTitle = "Test Document";
        String extractedText = "This is the OCR result text.";
        InputStream fileStream = new ByteArrayInputStream("PDF content".getBytes());

        when(minioService.downloadFile(filePath)).thenReturn(fileStream);
        when(ocrService.processDocument(any(), eq(language))).thenReturn(extractedText);
        doThrow(new RuntimeException("Database save error")).when(documentService).saveDocument(any(Document.class));

        workerService.processDocument(filePath, language, documentTitle);

        verify(minioService, times(1)).downloadFile(filePath);
        verify(ocrService, times(1)).processDocument(any(), eq(language));
        verify(documentService, times(1)).saveDocument(any(Document.class));
    }
}
