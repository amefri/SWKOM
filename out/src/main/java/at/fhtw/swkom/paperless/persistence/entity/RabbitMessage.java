package at.fhtw.swkom.paperless.persistence.entity;

public class RabbitMessage {
    private String title;
    private String fileName;
    private String author;
    private String fileContent; // Base64-encoded file content

    // Constructor
    public RabbitMessage(String title, String fileName, String author, String fileContent) {
        this.title = title;
        this.fileName = fileName;
        this.author = author;
        this.fileContent = fileContent;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAuthor() {
        return author;
    }

    public String getFileContent() {
        return fileContent;
    }
}
