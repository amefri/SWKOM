package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.services.dto.Document;
import at.fhtw.swkom.paperless.service.DocumentService;
import at.fhtw.swkom.paperless.service.mapper.DocumentMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("${openapi.paperlessRESTServer.base-path:}")
public class PaperlessApiController implements PaperlessApi {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;

    @Autowired
    public PaperlessApiController(DocumentService documentService, DocumentMapper documentMapper) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
    }

    @Override
    public ResponseEntity<Void> deleteDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        Optional<at.fhtw.swkom.paperless.persistence.entity.Document> documentEntity = documentService.getDocumentById(id);
        if (documentEntity.isPresent()) {
            documentService.deleteDocumentById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Document> getDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        Optional<at.fhtw.swkom.paperless.persistence.entity.Document> documentEntity = documentService.getDocumentById(id);
        return documentEntity.map(entity -> {
            Document dto = documentMapper.toDto(entity); // Convert entity to DTO
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<List<Document>> getDocuments() {
        List<at.fhtw.swkom.paperless.persistence.entity.Document> documentEntities = documentService.getAllDocuments();
        List<Document> documentDTOs = documentEntities.stream()
                .map(documentMapper::toDto) // Convert entities to DTOs
                .collect(Collectors.toList());
        return new ResponseEntity<>(documentDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> postDocument(
            @Parameter(name = "document", description = "") @Valid @RequestParam(value = "document", required = false) String document,
            @Parameter(name = "file", description = "") @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (document != null && !document.isBlank()) {
            at.fhtw.swkom.paperless.persistence.entity.Document newDocument = new at.fhtw.swkom.paperless.persistence.entity.Document();
            newDocument.setTitle(document);
            // Add other fields as needed and handle `file` if necessary
            documentService.saveDocument(newDocument);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Void> updateDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        Optional<at.fhtw.swkom.paperless.persistence.entity.Document> existingDocument = documentService.getDocumentById(id);
        if (existingDocument.isPresent()) {
            at.fhtw.swkom.paperless.persistence.entity.Document documentToUpdate = existingDocument.get();
            documentToUpdate.setTitle("Updated Title"); // Example
            documentService.saveDocument(documentToUpdate);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
