package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.persistence.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OcrService ocrService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, OcrService ocrService) {
        this.documentRepository = documentRepository;
        this.ocrService = ocrService;
    }

    // Retrieve a document by id
    public Optional<Document> getDocumentById(Integer id) {
        return documentRepository.findById(id);
    }

    // Retrieve all documents
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    // Save or update a document
    public void saveDocument(Document document) {
        documentRepository.save(document);
    }

    // Delete a document by id
    public void deleteDocumentById(Integer id) {
        documentRepository.deleteById(id);
    }

    // Process and save a document with OCR
    public Document processAndSaveDocument(File file, String title, String author, String language) {
        try {
            // Perform OCR on the file
            String extractedText = ocrService.processDocument(file, language);

            // Create a new Document entity
            Document document = new Document();
            document.setTitle(title);
            document.setAuthor(author);
            document.setContent(extractedText);

            // Save the document to the database
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process and save the document", e);
        }
    }
}
