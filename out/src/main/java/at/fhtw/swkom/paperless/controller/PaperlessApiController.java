package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.config.rabbitmq.RabbitMQProducer;
import at.fhtw.swkom.paperless.persistence.entity.RabbitMessage;
import at.fhtw.swkom.paperless.services.DocumentService;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mapper.DocumentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("${openapi.paperlessRESTServer.base-path:}")
public class PaperlessApiController implements PaperlessApi {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;
    private final RabbitMQProducer rabbitMQProducer;

    @Autowired
    public PaperlessApiController(DocumentService documentService,
                                  DocumentMapper documentMapper,
                                  RabbitMQProducer rabbitMQProducer) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
        this.rabbitMQProducer = rabbitMQProducer;
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
            @RequestParam(value = "document", required = false) String document,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            boolean isDocumentProvided = document != null && !document.isBlank();
            boolean isFileProvided = file != null && !file.isEmpty();

            if (!isDocumentProvided && !isFileProvided) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // No valid input
            }

            // If file is provided, process it
            if (isFileProvided) {
                String fileName = file.getOriginalFilename();
                byte[] fileBytes = file.getBytes();
                String fileContent = Base64.getEncoder().encodeToString(fileBytes); // Encode file as Base64
                System.out.println("File processed: " + fileName);

                // Send to RabbitMQ
                if (isDocumentProvided) {
                    RabbitMessage rabbitMessage = new RabbitMessage(document, fileName, fileContent);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonMessage = objectMapper.writeValueAsString(rabbitMessage);
                    System.out.println("Sending RabbitMQ message: " + jsonMessage); // Debug log
                    rabbitMQProducer.sendMessage(jsonMessage);
                }
            }

            // If document text is provided, save it as a document entity
            if (isDocumentProvided) {
                at.fhtw.swkom.paperless.persistence.entity.Document newDocument = new at.fhtw.swkom.paperless.persistence.entity.Document();
                newDocument.setTitle(document);
                newDocument.setCreated(LocalDateTime.now());
                // Optionally save file-related information if file is provided
                if (isFileProvided) {
                    // Add any necessary file metadata to the document entity
                }

                documentService.saveDocument(newDocument);
            }

            return new ResponseEntity<>(HttpStatus.CREATED); // Success
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
    public ResponseEntity<List<DocumentDTO>> getDocuments() {
        return new ResponseEntity<>(
                documentService.getAllDocuments().stream()
                        .map(documentMapper::toDto)
                        .collect(Collectors.toList()),
                HttpStatus.OK
        );
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
    }}