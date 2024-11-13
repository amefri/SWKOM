package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.services.dto.Document;


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
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-10-18T20:56:04.502791654Z[Etc/UTC]", comments = "Generator version: 7.10.0-SNAPSHOT")
@Controller
@RequestMapping("${openapi.paperlessRESTServer.base-path:}")
public class PaperlessApiController implements PaperlessApi {

    private final NativeWebRequest request;

    @Autowired
    public PaperlessApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Void> deleteDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        // TODO: Replace hc
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Document> getDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        // TODO: Replace hc
        Document body = new Document("Test Title");
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Document>> getDocuments() {
        // TODO: Replace
        Document doc1 = new Document("Test Title 1");
        Document doc2 = new Document("Test Title 2");
        List<Document> body = List.of(doc1, doc2);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> postDocument(
            @Parameter(name = "document", description = "") @Valid @RequestParam(value = "document", required = false) String document,
            @Parameter(name = "file", description = "") @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        // TODO: Replace HC
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateDocument(
            @Parameter(name = "id", description = "The id of the document", required = true, in = ParameterIn.PATH) @PathVariable("id") Integer id
    ) {
        // TODO
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
