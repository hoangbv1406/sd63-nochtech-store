package com.project.shopapp.shared.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseObject<T> {

    @JsonProperty("status")
    private int status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    public static <T> ResponseObject<T> success(T data) {
        return ResponseObject.<T>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ResponseObject<T> success(T data, String message) {
        return ResponseObject.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseObject<T> created(T data, String message) {
        return ResponseObject.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseObject<T> error(int status, String message) {
        return ResponseObject.<T>builder()
                .status(status)
                .message(message)
                .build();
    }

}
