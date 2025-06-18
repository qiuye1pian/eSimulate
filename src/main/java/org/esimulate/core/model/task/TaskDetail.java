package org.esimulate.core.model.task;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.pojo.pso.SimulateSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetail {

    List<Object> positionAndValue;

    @JSONField(serialize = false)
    private boolean isValid;

    @JSONField(serialize = false)
    private BigDecimal fitnessValue;

    public TaskDetail(SimulateSnapshot simulateSnapshot) {
        // 将整数坐标值转换为 BigDecimal 列表，并在末尾追加 fitness 值
        List<Object> list = new ArrayList<>();
        for (Integer val : simulateSnapshot.getCurrentPosition().getCoordinateValueList()) {
            list.add(BigDecimal.valueOf(val).setScale(0, RoundingMode.HALF_UP));
        }
        // 格式化为千分位，两位小数，并添加“元”单位
        DecimalFormat df = new DecimalFormat("#,##0.00");
        list.add(df.format(simulateSnapshot.getFitnessValue()) + " 元");
        list.add(simulateSnapshot.getIsValid());
        list.add(simulateSnapshot.getMessage());
        this.isValid = simulateSnapshot.getIsValid();
        this.fitnessValue = simulateSnapshot.getFitnessValue();
        this.positionAndValue = list;
    }

}
