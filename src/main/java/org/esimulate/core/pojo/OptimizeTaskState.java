package org.esimulate.core.pojo;

import lombok.Data;
import org.esimulate.core.model.enums.TaskStateEnum;
import org.esimulate.core.model.task.OptimizeTask;

@Data
public class OptimizeTaskState {

    private TaskStateEnum taskState = TaskStateEnum.PENDING;

    /**
     * 当前已完成的迭代次数
     */
    private int currentIteration = 0;

    /**
     * 总迭代次数（或总步数）
     */
    private int totalIterations = 0;

    public OptimizeTaskState(OptimizeTask optimizeTask) {
        this.taskState = optimizeTask.getTaskState();
        this.currentIteration = optimizeTask.getCurrentIteration();
        this.totalIterations = optimizeTask.getTotalIterations();
    }
}
