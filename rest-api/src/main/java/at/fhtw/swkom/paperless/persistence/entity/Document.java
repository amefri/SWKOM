package at.fhtw.swkom.paperless.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
