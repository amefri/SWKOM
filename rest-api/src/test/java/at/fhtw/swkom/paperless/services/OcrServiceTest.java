package at.fhtw.swkom.paperless.services;

import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OcrServiceTest {

    @Mock
    private Tesseract tesseract;

    @InjectMocks
    private OcrService ocrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessDocument_success() throws Exception {
        // Test for successful OCR processing
        File mockFile = mock(File.class);
        String language = "eng";
        String expectedText = "This is the extracted text.";

        when(tesseract.doOCR(mockFile)).thenReturn(expectedText);

        String result = ocrService.processDocument(mockFile, language);

        assertEquals(expectedText, result);
        verify(tesseract, times(1)).setLanguage(language);
        verify(tesseract, times(1)).doOCR(mockFile);
    }


    @Test
    void testStreamToFile_success() throws Exception {
        // Test for successfully converting InputStream to a temporary file
        String content = "This is a test content.";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String filename = "testFile";

        File result = OcrService.streamToFile(inputStream, filename);

        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.getName().startsWith(filename));
        assertTrue(result.getName().endsWith(".pdf"));

        // Cleanup the temp file
        result.delete();
    }

    @Test
    void testStreamToFile_failure() {
        // Test for failure in InputStream to File conversion (e.g., invalid input)
        InputStream inputStream = null; // Null input stream to simulate failure
        String filename = "testFile";

        Exception exception = assertThrows(Exception.class, () ->
                OcrService.streamToFile(inputStream, filename)
        );

        assertNotNull(exception);
    }
}
