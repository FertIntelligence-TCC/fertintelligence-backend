package com.migueltcc.fertintelligence.handler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice // Indica que esta classe tratará exceções globalmente
public class GlobalExceptionHandler {

    // --- Handler para Entidade Não Encontrada ---
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // Mensagem da exceção (ex: "Propriedade não encontrada...")
        body.put("path", request.getDescription(false).substring(4)); // URI da requisição

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // --- Handler para Entidade Já Existente ---
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExistsException(EntityExistsException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage()); // Mensagem da exceção (ex: "CNPJ já existe...")
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // --- Handler para Acesso Negado (Permissões) ---
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage()); // Mensagem da exceção (ex: "Acesso negado...")
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // --- Handler para Erros de Validação (@Valid) ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");
        // Coleta todas as mensagens de erro de validação
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        body.put("message", errors);
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    // --- Handler Genérico para Outras Exceções (Opcional, mas recomendado) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        // Logar a exceção aqui é uma boa prática
        // logger.error("Unhandled exception occurred", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Ocorreu um erro inesperado: " + ex.getMessage());
        body.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}