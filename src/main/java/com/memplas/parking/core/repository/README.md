# Repository
JPA repositories and custom query implementations.

## Purpose
- Abstract data access layer
- Provide CRUD operations
- Define custom queries
- Handle database interactions

## Contains
- JPA Repository interfaces
- Custom query methods
- Native queries if needed
- Repository implementations

## Example
```java
@Repository
public interface CoreRepository extends JpaRepository<CoreEntity, Long> {
    Optional<CoreEntity> findByName(String name);
    List<CoreEntity> findByStatus(CoreStatus status);
}
```
