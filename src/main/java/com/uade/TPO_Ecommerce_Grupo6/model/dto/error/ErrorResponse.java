package com.uade.TPO_Ecommerce_Grupo6.model.dto.error;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Estructura estándar para respuestas de error.
 *
 * Utilizada por GlobalExceptionHandler para devolver errores en formato JSON consistente.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String error;
    private LocalDateTime timestamp;
    private String path;
    private int status;

    public ErrorResponse(String error, String path, int status) {
        this.error = error;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.status = status;
    }
}
