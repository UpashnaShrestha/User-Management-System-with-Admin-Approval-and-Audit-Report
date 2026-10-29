package com.project.UserManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T>{
    private String message;
    private Boolean success;
    private T data;

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(message, true, data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(message, false, null);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(message, false, null);
    }
}
