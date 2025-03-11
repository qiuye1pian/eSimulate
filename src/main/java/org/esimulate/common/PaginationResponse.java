package org.esimulate.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationResponse<T> {
    private List<T> content; // 当前页数据
    private long totalElements; // 总数据量
    private int totalPages; // 总页数
    private int currentPage; // 当前页
}