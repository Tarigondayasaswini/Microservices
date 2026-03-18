package com.revplay.catalogservice.common.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<FieldError> errors;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <S> ApiResponse<S> success(S data, String message) {
        return ApiResponse.<S>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <S> ApiResponse<S> success(String message) {
        return ApiResponse.<S>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <S> ApiResponse<S> error(String message) {
        return ApiResponse.<S>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <S> ApiResponse<S> error(String message, List<FieldError> errors) {
        return ApiResponse.<S>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}
