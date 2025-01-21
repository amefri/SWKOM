package at.fhtw.swkom.paperless.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;




@Entity
@Table(name = "document")
@EntityListeners(AuditingEntityListener.class)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column
    private String author;

    @Column
    private String content; // Field to store OCR-extracted text

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime created;

    @Column
    private String minioFileName;


    public Document(String title, String author, String content, LocalDateTime created) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.created = created;
    }
    public Document() {
    }

    public Document(Integer id, String title, String author, String content, LocalDateTime created, String minioFileName) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.created = created;
        this.minioFileName = minioFileName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getMinioFileName() {
        return minioFileName;
    }

    public void setMinioFileName(String minioFileName) {
        this.minioFileName = minioFileName;
    }
}
