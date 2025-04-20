package org.esimulate.core.pojo.simulate.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StackedChartData {

    String name;

    List<BigDecimal> seriesData;

    Integer priority = 500;

}
