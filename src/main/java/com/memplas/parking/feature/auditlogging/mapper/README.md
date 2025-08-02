# Mapper
MapStruct or manual mappers for converting between DTOs and Entities.

## Purpose
- Convert between DTOs and domain models
- Handle data transformation logic
- Maintain separation of concerns
- Reduce boilerplate code

## Preferred: MapStruct
```java
@Mapper(componentModel = "spring")
public interface AuditloggingMapper {
    AuditloggingResponseDto toDto(AuditloggingEntity entity);
    AuditloggingEntity toEntity(CreateAuditloggingRequest request);
}
```
