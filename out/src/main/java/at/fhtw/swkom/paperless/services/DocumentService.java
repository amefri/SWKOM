package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.persistence.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
}
