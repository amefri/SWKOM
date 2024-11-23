package at.fhtw.swkom.paperless.persistence.entity;

public class RabbitMessage {
    private String title;
    private String fileName;
    private String fileContent; // Base64-encoded file content

    public RabbitMessage(String title, String fileName, String fileContent) {
        this.title = title;
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }
}
