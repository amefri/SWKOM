package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

@Service
public class WorkerService {
//Coordinates file retrieval, OCR processing and database storage

    private final MinioService minioService;
    private final OcrService ocrService;
    private final DocumentService documentService;

    @Autowired
    public WorkerService(MinioService minioService, OcrService ocrService, DocumentService documentService) {
        this.minioService = minioService;
        this.ocrService = ocrService;
        this.documentService = documentService;
    }

    public void processDocument(String filePath, String language, String documentTitle) {
        try {
            // Fetch file from MinIO
            InputStream fileStream = minioService.downloadFile(filePath);

            // Perform OCR processing
            String extractedText = ocrService.processDocument(OcrService.streamToFile(fileStream, "tempFile"), language);
            System.out.println("Extracted Text: " + extractedText);

            // Save extracted text to database
            Document document = new Document();
            document.setTitle(documentTitle);
            document.setContent(extractedText);
            documentService.saveDocument(document);

            System.out.println("Document processed and saved: " + documentTitle);

        } catch (Exception e) {
            System.err.println("Error processing document: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
