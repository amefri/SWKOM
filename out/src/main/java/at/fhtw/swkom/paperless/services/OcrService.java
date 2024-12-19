package at.fhtw.swkom.paperless.services;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    public String processDocument(File file, String language) {
        try {
            tesseract.setLanguage(language);
            return tesseract.doOCR(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process document with OCR", e);
        }
    }
    public static File streamToFile(InputStream in, String filename, String fileExtension) throws Exception {
        File tempFile = File.createTempFile(filename, fileExtension);
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }


}
