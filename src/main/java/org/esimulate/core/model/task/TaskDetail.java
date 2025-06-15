package org.esimulate.core.model.task;

import lombok.*;
import org.esimulate.core.pojo.pso.SimulateSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetail {

    List<Object> positionAndValue;

    String sortKey;

    public TaskDetail(SimulateSnapshot simulateSnapshot) {
        // 将整数坐标值转换为 BigDecimal 列表，并在末尾追加 fitness 值
        List<Object> list = new ArrayList<>();
        for (Integer val : simulateSnapshot.getCurrentPosition().getCoordinateValueList()) {
            list.add(BigDecimal.valueOf(val).setScale(0, RoundingMode.HALF_UP));
        }
        list.add(simulateSnapshot.getFitnessValue());
        list.add(simulateSnapshot.getIsValid());
        list.add(simulateSnapshot.getMessage());
        sortKey = String.format("%s-%s", simulateSnapshot.getIsValid() ? 0 : 1, simulateSnapshot.getFitnessValue());
        this.positionAndValue = list;
    }

}
