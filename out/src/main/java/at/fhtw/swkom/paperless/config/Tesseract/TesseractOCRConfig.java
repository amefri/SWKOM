package at.fhtw.swkom.paperless.config.Tesseract;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractOCRConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        // Path to tessdata files in the Docker container
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        tesseract.setLanguage("eng+deu"); // Support both English and German
        return tesseract;
    }
}
