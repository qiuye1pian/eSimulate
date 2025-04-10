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
        this.yAxis = new YAxis<>(yAxisMax);
        this.series = Collections.singletonList(new Series<>(seriesData));
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
        private final String stack = "Total";
        private final AreaStyle areaStyle = new AreaStyle();
        private final Emphasis emphasis = new Emphasis();
        private List<Y> data;

        @Getter
        @Setter
        public static class AreaStyle {
        }

        @Getter
        @Setter
        public static class Emphasis {
            private Focus focus = new Focus();

            @Getter
            @Setter
            public static class Focus {
                private String focus = "series";
            }
        }
    }

}