package org.esimulate.core.pojo.chart.line;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public abstract class ChartLineDto<X, Y> {

    protected XAxis<X> xAxis;
    protected YAxis<Y> yAxis;
    protected List<Series<Y>> series;

    protected void init(List<X> xAxisData, List<Y> seriesData, Y yAxisMax) {
        this.xAxis = new XAxis<>(xAxisData);
        this.series = Collections.singletonList(new Series<>(seriesData));
        this.yAxis = new YAxis<>(yAxisMax);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class XAxis<X> {
        private final String type = "category";
        private final boolean boundaryGap = false;
        private final AxisLabel axisLabel = new AxisLabel();
        private List<X> data;

        @Getter
        @Setter
        public static class AxisLabel {
            private String formatter = "{value}";
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class YAxis<Y> {
        private Y max;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Series<Y> {
        private final String name = "";
        private final String type = "line";
        private final Boolean smooth = true;
        private List<Y> data;
    }

}