package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class WorkerService {

    private final MinioService minioService;
    private final OcrService ocrService;
    private final DocumentService documentService;

    @Autowired
    public WorkerService(MinioService minioService, OcrService ocrService, DocumentService documentService) {
        this.minioService = minioService;
        this.ocrService = ocrService;
        this.documentService = documentService;
    }

    public void processAndUploadDocument(String filePath, String language, String documentTitle, String fileExtension) {
        try (InputStream fileStream = minioService.downloadFile(filePath)) {

            // Step 2: Convert InputStream to a File
            File tempFile = OcrService.streamToFile(fileStream, "tempFile", fileExtension);

            // Step 3: Optional OCR Processing
            String extractedText = null;
            if (isOcrSupported(fileExtension)) {
                extractedText = ocrService.processDocument(tempFile, language);
                System.out.println("Extracted Text: " + extractedText);

                // Save the OCR result to the database
                Document document = new Document();
                document.setTitle(documentTitle);
                document.setContent(extractedText);
                documentService.saveDocument(document);
                System.out.println("Document saved in the database: " + documentTitle);
            }

            // Step 4: Reset stream if needed or recreate it
            InputStream uploadStream = minioService.downloadFile(filePath);
            String uploadedFileName = "uploaded_" + documentTitle + fileExtension;

            String contentType = getContentType(fileExtension);
            minioService.uploadFile(uploadedFileName, uploadStream, uploadStream.available(), contentType);
            System.out.println("File uploaded to MinIO: " + uploadedFileName);

        } catch (Exception e) {
            System.err.println("Error processing and uploading document: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getContentType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".tif":
            case ".tiff":
                return "image/tiff";
            case ".pdf":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }


    private boolean isOcrSupported(String fileExtension) {
        // Check if OCR is supported for the given file type
        return fileExtension.equalsIgnoreCase(".jpg") || fileExtension.equalsIgnoreCase(".jpeg") || fileExtension.equalsIgnoreCase(".png") || fileExtension.equalsIgnoreCase(".tif") || fileExtension.equalsIgnoreCase(".tiff") || fileExtension.equalsIgnoreCase(".pdf");
    }
}
