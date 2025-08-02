# Model
Domain models including Entities, Enums, and Value Objects.

## Purpose
- Define core business entities
- Represent database structure
- Encapsulate business rules
- Define value objects and enums

## Contains
- JPA Entities (@Entity)
- Enums for status/types
- Value Objects
- Domain-specific exceptions

## Example
```java
@Entity
@Table(name = "core")
public class CoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // fields, getters, setters
}
```
