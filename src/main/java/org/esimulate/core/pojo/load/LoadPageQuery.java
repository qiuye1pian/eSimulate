package org.esimulate.core.pojo.load;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esimulate.common.PageQuery;

@NoArgsConstructor
@Getter
@Setter
public class LoadPageQuery extends PageQuery {

    private String schemeName;

    @Override
    public String getDefaultSortByProperty() {
        return "id";
    }
}
