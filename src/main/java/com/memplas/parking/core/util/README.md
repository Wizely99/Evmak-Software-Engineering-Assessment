# Util
Utility functions and helper classes specific to this feature.

## Purpose
- Common utility functions
- Helper methods
- Constants and enums
- Validation utilities

## Contains
- Utility classes
- Helper methods
- Constants
- Common validations

## Example
```java
public class CoreUtils {
    public static final String DEFAULT_STATUS = "ACTIVE";
    
    public static boolean isValidCore(String name) {
        return name != null && name.length() > 3;
    }
}
```
