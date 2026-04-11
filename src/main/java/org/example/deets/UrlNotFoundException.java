package org.example.deets;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UrlNotFoundException extends RuntimeException{

    private final String errorCode = "URL_NOT_FOUND";
    private final HttpStatus statusCode = HttpStatus.NOT_FOUND;
    public UrlNotFoundException(String message){
        super(message);
    }
}
