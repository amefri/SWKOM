package at.fhtw.swkom.paperless.services;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    public String processDocument(File file, String language) {
        try {
            // Dynamically set the language for OCR
            tesseract.setLanguage(language);
            return tesseract.doOCR(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process document with OCR", e);
        }
    }
}
