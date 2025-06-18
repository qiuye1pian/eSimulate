package org.esimulate.core.pojo.pso;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.task.OptimizeTask;
import org.esimulate.core.model.task.TaskDetail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class OptimizeResultDto {

    private List<String> positionTitle = new ArrayList<>();
    private List<TaskDetail> taskDetailList = new ArrayList<>();

    public OptimizeResultDto(OptimizeTask optimizeTask) {
        this.positionTitle = optimizeTask.getPositionTitle();
        this.taskDetailList = optimizeTask.getTaskDetailList().stream()
                .sorted(Comparator.comparing(TaskDetail::isValid).reversed()
                        .thenComparing(TaskDetail::getFitnessValue))
                .collect(Collectors.toList());
    }

}
