package org.esimulate.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
@Data
public abstract class PageQuery {

    protected int page = 1;

    protected int size = 10;

    //排序的字段
    protected String sort;

    //DESC ASC
    protected String sortDirection = "DESC";

    /**
     * 默认排序字段 每个查询需要指定默认排序字段
     *
     * @return 默认排序字段名
     */
    public abstract String getDefaultSortByProperty();

    /**
     * 获取排序字段，如果参数传入值(sortByProperty)存在则使用传入值，否则使用默认值
     *
     * @return 排序字段
     */
    public final String getSort() {
        return StringUtils.isNotEmpty(this.sort) ? this.sort : getDefaultSortByProperty();
    }

    public Pageable toPageable() throws RuntimeException {
        if (isBadParam(page) || isBadParam(size)) {
            throw new RuntimeException("page 和 size 必须为大于0的整数");
        }
        if (StringUtils.isNotEmpty(this.getSort()) && StringUtils.isNotEmpty(sortDirection)) {
            Sort.Order order = new Sort.Order(Sort.Direction.valueOf(this.getSortDirection()), this.getSort());
            Sort sort = Sort.by(order);
            return PageRequest.of(this.getPage() - 1, this.getSize(), sort);
        }
        return PageRequest.of(this.getPage() - 1, this.getSize());

    }

    private boolean isBadParam(Integer param) {
        return null == param || param <= 0;
    }

}
