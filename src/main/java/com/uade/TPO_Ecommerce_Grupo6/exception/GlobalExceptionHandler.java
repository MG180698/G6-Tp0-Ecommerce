package com.uade.TPO_Ecommerce_Grupo6.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.error.ErrorResponse;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 NOT FOUND

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProductoNoEncontrado(
            ProductoNoEncontradoException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaNoEncontrada(
            CategoriaNoEncontradaException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarritoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleCarritoNoEncontrado(
            CarritoNoEncontradoException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemCarritoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleItemCarritoNoEncontrado(
            ItemCarritoNoEncontradoException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //400 BAD REQUEST

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDatoDuplicado(
            DatoDuplicadoException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(
            StockInsuficienteException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarritoYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleCarritoYaExiste(
            CarritoYaExisteException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 422 UNPROCESSABLE ENTITY

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        // Compilar todos los mensajes de error de validación
        String mensajeError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                "Validación fallida: " + mensajeError,
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.UNPROCESSABLE_ENTITY.value()
        );

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // 404 NOT FOUND

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                "Endpoint no encontrado: " + ex.getRequestURL(),
                ex.getRequestURL(),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 500 INTERNAL SERVER ERROR

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        // Log al error (en producción, usar logger)
        System.err.println("Excepción no manejada: " + ex.getClass().getSimpleName());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "Error interno del servidor",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
