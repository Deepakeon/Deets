package org.example.deets.exceptions;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final Object details;
}
