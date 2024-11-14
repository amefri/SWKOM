package at.fhtw.swkom.paperless.services.mapper;

import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.persistence.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    // Mapping from entity to DTO
    DocumentDTO toDto(Document document);

    // Mapping from DTO to entity
    Document toEntity(DocumentDTO document);
}

