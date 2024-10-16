package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.services.dto.Document;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Validated
public interface PaperlessApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Operation(
            summary = "Delete a document by id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Document does not exist with this id!")
            }
    )
    @DeleteMapping("/api/documents/{id}")
    default ResponseEntity<Void> deleteDocument(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            summary = "Retrieve a document by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Document does not exist with this id!")
            }
    )
    @GetMapping(value = "/api/documents/{id}", produces = "application/json")
    default ResponseEntity<Document> getDocument(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id) {
        getRequest().ifPresent(request -> {
            PaperlessUtil.setExampleResponse(request, "application/json", "{\"id\": 1, \"title\": \"Sample Title\", \"author\": \"Author\", \"created\": \"2024-10-16\"}");
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            summary = "Return a list of documents",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Document.class)))
                    })
            }
    )
    @GetMapping(value = "/api/documents", produces = "application/json")
    default ResponseEntity<List<Document>> getDocuments() {
        getRequest().ifPresent(request -> {
            PaperlessUtil.setExampleResponse(request, "application/json", "[{\"id\": 1, \"title\": \"Sample Title\", \"author\": \"Author\", \"created\": \"2024-10-16\"}]");
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            summary = "Upload a document",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document successfully uploaded."),
                    @ApiResponse(responseCode = "400", description = "Document upload failed due to bad request!")
            }
    )
    @PostMapping(value = "/api/documents/post_document", consumes = "multipart/form-data")
    default ResponseEntity<Void> postDocument(
            @RequestParam(value = "document", required = false) String document,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
