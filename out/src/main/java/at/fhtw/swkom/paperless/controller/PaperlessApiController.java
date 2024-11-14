package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.services.DocumentService;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mapper.DocumentMapper;
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

import java.io.IOException;
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
    public ResponseEntity<DocumentDTO> getDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        Optional<at.fhtw.swkom.paperless.persistence.entity.Document> documentEntity = documentService.getDocumentById(id);
        return documentEntity.map(entity -> {
            DocumentDTO dto = documentMapper.toDto(entity); // Convert entity to DTO
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Void> postDocument(
            @Parameter(name = "document", description = "") @Valid @RequestParam(value = "document", required = false) String document,
            @Parameter(name = "file", description = "") @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            if ((document != null && !document.isBlank()) || file != null) {
                // Create new document entity
                at.fhtw.swkom.paperless.persistence.entity.Document newDocument = new at.fhtw.swkom.paperless.persistence.entity.Document();
                if (document != null) {
                    newDocument.setTitle(document);
                }

                if (file != null && !file.isEmpty()) {
                    byte[] fileBytes = file.getBytes();
                    String originalFileName = file.getOriginalFilename();
                    // You can add logic to process/store the file here
                    System.out.println("File processed: " + originalFileName);
                }

                // Save the document
                documentService.saveDocument(newDocument);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle unexpected errors
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    public ResponseEntity<Void> updateDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        Optional<at.fhtw.swkom.paperless.persistence.entity.Document> existingDocument = documentService.getDocumentById(id);
        if (existingDocument.isPresent()) {
            at.fhtw.swkom.paperless.persistence.entity.Document documentToUpdate = existingDocument.get();
            documentToUpdate.setTitle("Updated Title");
            documentService.saveDocument(documentToUpdate);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}