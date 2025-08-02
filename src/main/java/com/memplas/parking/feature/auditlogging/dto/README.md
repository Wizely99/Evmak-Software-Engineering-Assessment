# DTO (Data Transfer Objects)
Request and response models for API communication.

## Purpose
- Define API request/response structures
- Separate external API contracts from internal models
- Provide validation annotations
- Enable API versioning

## Structure
- Request DTOs: For incoming data
- Response DTOs: For outgoing data
- Validation annotations (@Valid, @NotNull, etc.)

## Example
```java
public class CreateAuditloggingRequest {
    @NotBlank
    private String name;
    // fields and validation
}
```
