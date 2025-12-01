package com.project.shopapp.shared.exceptions;

import com.project.shopapp.shared.base.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Void>> handleGeneralException(Exception exception) {
        log.error("Internal Server Error: ", exception);
        return ResponseEntity.internalServerError().body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Lỗi hệ thống không xác định. Vui lòng thử lại sau!")
                        .build()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseObject<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.warn("Data Integrity Violation: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("Dữ liệu đã tồn tại hoặc đang bị ràng buộc bởi hệ thống.")
                        .build()
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ResponseObject<Void>> handleOptimisticLockingException(ObjectOptimisticLockingFailureException exception) {
        log.warn("Race condition detected: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("Hệ thống đang quá tải hoặc dữ liệu vừa được cập nhật bởi người khác. Vui lòng thử lại!")
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String key = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            errors.put(key, error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(
                ResponseObject.<Map<String, String>>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Dữ liệu đầu vào không hợp lệ")
                        .data(errors)
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseObject<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("Bạn không có quyền thực hiện hành động này!")
                        .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseObject<Void>> handleAuthenticationException(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Vui lòng đăng nhập để tiếp tục!")
                        .build()
        );
    }

    @ExceptionHandler({InvalidParamException.class, InvalidPasswordException.class})
    public ResponseEntity<ResponseObject<Void>> handleBadRequestCustomExceptions(RuntimeException exception) {
        return ResponseEntity.badRequest().body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseObject<Void>> handleDataNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseObject<Void>> handleConflictException(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler({ForbiddenException.class, PermissionDenyException.class})
    public ResponseEntity<ResponseObject<Void>> handleForbiddenCustomExceptions(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ResponseObject<Void>> handleExpiredTokenException(ExpiredTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseObject.<Void>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message(exception.getMessage())
                        .build()
        );
    }
}
