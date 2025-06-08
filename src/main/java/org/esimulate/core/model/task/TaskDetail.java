package org.esimulate.core.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.pso.SimulateSnapshot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetail {

    List<BigDecimal> positionAndValue;

    public TaskDetail(SimulateSnapshot x) {
        // 将整数坐标值转换为 BigDecimal 列表，并在末尾追加 fitness 值
        List<BigDecimal> list = new ArrayList<>();
        for (Integer val : x.getCurrentPosition().getCoordinateValueList()) {
            list.add(BigDecimal.valueOf(val));
        }
        list.add(x.getFitnessValue());
        this.positionAndValue = list;
    }
}
