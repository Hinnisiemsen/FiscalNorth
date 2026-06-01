package de.fiscalnorth.shared;

import lombok.Getter;

@Getter
public class RessourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public RessourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
