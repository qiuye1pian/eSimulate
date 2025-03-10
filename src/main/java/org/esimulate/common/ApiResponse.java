package org.esimulate.common;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;  // 状态码
    private String message;  // 提示信息
    private T data;  // 返回数据

    // 构造方法
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功返回
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "返回", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    // 失败返回
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
