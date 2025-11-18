package de.fiscalnorth.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RessourceNotFoundException extends RuntimeException {
    public RessourceNotFoundException(String ressourceName, String fieldName, Object fieldValue) {
        super(String.format("%s nicht gefunden mit %s: '%s'", ressourceName, fieldName, fieldValue));
    }
}
