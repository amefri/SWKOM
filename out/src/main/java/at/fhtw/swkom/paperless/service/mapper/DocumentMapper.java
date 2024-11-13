package at.fhtw.swkom.paperless.service.mapper;

import at.fhtw.swkom.paperless.persistence.entity.Document;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    // Mapping from entity to DTO
    at.fhtw.swkom.paperless.services.dto.Document toDto(at.fhtw.swkom.paperless.persistence.entity.Document document);

    // Mapping from DTO to entity
    at.fhtw.swkom.paperless.persistence.entity.Document toEntity(at.fhtw.swkom.paperless.services.dto.Document document);
}
