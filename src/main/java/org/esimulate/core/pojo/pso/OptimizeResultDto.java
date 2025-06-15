package org.esimulate.core.pojo.pso;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.task.OptimizeTask;
import org.esimulate.core.model.task.TaskDetail;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class OptimizeResultDto {
//    private Position globalBestPosition;
//    private BigDecimal globalBestFitness;
    private List<String> positionTitle = new ArrayList<>();
    private List<TaskDetail> taskDetailList = new ArrayList<>();

    public OptimizeResultDto(OptimizeTask optimizeTask) {
//        this.globalBestPosition = optimizeTask.getGlobalBestPosition();
//        this.globalBestFitness = optimizeTask.getGlobalBestValue();
        this.positionTitle = optimizeTask.getPositionTitle();
        this.taskDetailList = optimizeTask.getTaskDetailList();
    }
}
