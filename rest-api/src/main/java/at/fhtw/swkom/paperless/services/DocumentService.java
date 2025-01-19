package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.persistence.repository.DocumentRepository;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mapper.DocumentMapper;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OcrService ocrService;
    private final MinioService minioService;
    private final RabbitMQService rabbitMQService;
    private final DocumentMapper mapper;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, OcrService ocrService, MinioService minioService, RabbitMQService rabbitMQService, DocumentMapper mapper) {
        this.documentRepository = documentRepository;
        this.ocrService = ocrService;
        this.minioService = minioService;
        this.rabbitMQService = rabbitMQService;
        this.mapper = mapper;
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

    public Optional<DocumentDTO> create(String document, MultipartFile file) {
        final String fileNameBucket;
        try {
            fileNameBucket = minioService.uploadFile(file.getOriginalFilename(), file.getInputStream(), "application/pdf");
            rabbitMQService.sendToOCRWorker(fileNameBucket);
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.severe(e.getMessage());
            log.severe("Failed to upload document to minio");
            return Optional.empty();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return Optional.empty();
        }
        final Document toBeSaved = new Document(null, document, document, null, LocalDateTime.now(), fileNameBucket);
        documentRepository.save(toBeSaved);
        final Optional<Document> model = documentRepository.findById(toBeSaved.getId());
        return model.map(mapper::toDto);
    }
}
