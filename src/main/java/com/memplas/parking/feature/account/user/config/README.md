# Config
Feature-specific configuration classes.

## Purpose
- Module-specific configurations
- Security configurations
- Caching configurations
- Custom beans and components

## Contains
- @Configuration classes
- Security configs
- Cache configurations
- Custom validators

## Example
```java
@Configuration
public class UserConfig {
    @Bean
    public UserValidator userValidator() {
        return new UserValidator();
    }
}
```
