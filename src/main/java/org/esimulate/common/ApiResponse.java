package org.esimulate.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    // 状态码
    private int code;

    // 提示信息
    private String message;

    // 是否成功
    private Boolean success;

    // 返回数据
    private T data;

    // 构造方法
    private ApiResponse(int code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    // 成功返回
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "返回", data, true);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, true);
    }

    // 失败返回
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null,false);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null,false);
    }
}
