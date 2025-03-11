package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.esimulate.common.PageQuery;

@AllArgsConstructor
@Getter
public class WindPowerPageQuery extends PageQuery {

    private String windPowerName;


    public WindPowerPageQuery(String windPowerName, int page, int size) {
        super(page, size);
    }


    public WindPowerPageQuery(int page, int size) {
        super(page, size);
    }
}
