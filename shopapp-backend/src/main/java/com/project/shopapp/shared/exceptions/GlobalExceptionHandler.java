package com.project.shopapp.shared.exceptions;

import com.project.shopapp.responses.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception exception) {
        log.error("Internal Server Error: ", exception);
        return ResponseEntity.internalServerError().body(
                ResponseObject.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("Lỗi hệ thống không xác định. Vui lòng thử lại sau!")
                        .build()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseObject> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("Data Integrity Violation: ", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseObject.builder()
                        .status(HttpStatus.CONFLICT)
                        .message("Dữ liệu đã tồn tại hoặc đang bị ràng buộc bởi dữ liệu khác.") // Giấu SQL Error
                        .build()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseObject> handleConflictException(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseObject.builder()
                        .status(HttpStatus.CONFLICT)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseObject> handleForbiddenException(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseObject.builder()
                        .status(HttpStatus.FORBIDDEN)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseObject> handleDataNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ResponseObject> handleInvalidParamException(InvalidParamException exception) {
        return ResponseEntity.badRequest().body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ResponseObject> handleInvalidPasswordException(InvalidPasswordException exception) {
        return ResponseEntity.badRequest().body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(PermissionDenyException.class)
    public ResponseEntity<ResponseObject> handlePermissionDenyException(PermissionDenyException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseObject.builder()
                        .status(HttpStatus.FORBIDDEN)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ResponseObject> handleExpiredTokenException(ExpiredTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseObject.builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValidationExceptions(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(String.join("; ", errors))
                        .build()
        );
    }

}
