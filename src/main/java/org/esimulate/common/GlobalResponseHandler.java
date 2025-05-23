package org.esimulate.common;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {

        // 1️⃣ 跳过已经是 ApiResponse 的
        return !returnType.getParameterType().equals(ApiResponse.class);

    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {
        // 如果返回 null，封装为 "成功但无数据"
        if (body == null) {
            return ApiResponse.success("成功", null);
        }

        // 如果返回值是异常，交给 `GlobalExceptionHandler` 处理
        if (body instanceof Exception) {
            return ApiResponse.error("服务器异常：" + ((Exception) body).getMessage());
        }

        if (body instanceof Page) {
            return ApiResponse.page((Page<?>) body);
        }

        //下载文件的时候跳过封装
        if (selectedContentType.includes(MediaType.APPLICATION_OCTET_STREAM) ||
                selectedContentType.includes(MediaType.valueOf("text/csv")) ||
                selectedContentType.includes(MediaType.APPLICATION_PDF)) {
            return body;
        }

        // 否则，封装为 ApiResponse
        return ApiResponse.success(body);
    }
}
