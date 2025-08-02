# Service
Business logic and service layer implementations.

## Purpose
- Implement business logic
- Coordinate between repositories
- Handle transactions
- Validate business rules

## Contains
- Service interfaces
- Service implementations
- Business validation
- Transaction management

## Example
```java
@Service
@Transactional
public class CoreService {
    private final CoreRepository repository;
    
    public CoreResponseDto createCore(CreateCoreRequest request) {
        // business logic
    }
}
```
