package org.esimulate.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        log.error("服务器内部错误：", e);
        return ApiResponse.error("服务器内部错误：" + e.getMessage());
    }
}
