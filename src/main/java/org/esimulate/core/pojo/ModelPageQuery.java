package org.esimulate.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.esimulate.common.PageQuery;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public abstract class ModelPageQuery extends PageQuery {

    private String modelName;

}
