package org.esimulate.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

@NoArgsConstructor
@Getter
public abstract class PageQuery {

    protected int page = 0;

    protected int size = 10;

    public PageQuery(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, getSort());
    }

    public Sort getSort() {
        return Sort.by(Sort.Direction.fromString(getSortDirection()), getSortBy());
    }

    protected String getSortBy() {
        return "id";
    }

    protected String getSortDirection() {
        return "desc";
    }

}
