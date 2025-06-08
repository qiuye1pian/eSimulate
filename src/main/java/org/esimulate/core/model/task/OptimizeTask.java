package org.esimulate.core.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.converter.ListStringConverter;
import org.esimulate.core.converter.ListTaskDetailConverter;
import org.esimulate.core.converter.PositionConverter;
import org.esimulate.core.converter.PsoConfigConverter;
import org.esimulate.core.model.enums.TaskStateEnum;
import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.pso.SimulateSnapshot;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.pso.particle.Position;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "Optimize_Task")
@AllArgsConstructor
@NoArgsConstructor
public class OptimizeTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Convert(converter = PsoConfigConverter.class)
    @Column(name = "pso_config", columnDefinition = "TEXT")
    private PsoConfig psoConfig;

    @Lob
    @Convert(converter = ListStringConverter.class)
    @Column(name = "position_title", columnDefinition = "TEXT")
    private List<String> positionTitle = new ArrayList<>();

//    @OneToMany(mappedBy = "simulateTask", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Lob
    @Convert(converter = ListTaskDetailConverter.class)
    @Column(name = "taskDetail_list", columnDefinition = "TEXT")
    List<TaskDetail> taskDetailList = new ArrayList<>();

    @Lob
    @Convert(converter = PositionConverter.class)
    @Column(name = "global_BestPosition", columnDefinition = "TEXT")
    private Position globalBestPosition;

    @Column(name = "global_BestValue")
    private BigDecimal globalBestValue;

    @Column
    private TaskStateEnum taskState = TaskStateEnum.PENDING;

    /**
     * 当前已完成的迭代次数
     */
    @Column(name = "current_iteration")
    private int currentIteration = 0;

    /**
     * 总迭代次数（或总步数）
     */
    @Column(name = "total_iterations")
    private int totalIterations = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public OptimizeTask(PsoConfig psoConfig) {
        this.psoConfig = psoConfig;
        this.totalIterations = psoConfig.getMaxIterations() * psoConfig.getParticleCount();
    }

    public void setOptimizeResult(OptimizeResult optimizeResult) {
        this.globalBestPosition = optimizeResult.getGlobalBestPosition();
        this.globalBestValue = optimizeResult.getGlobalBestValue();
        List<SimulateSnapshot> simulateSnapshotList = optimizeResult.getSimulateSnapshotList();
        List<String> titleList = simulateSnapshotList
                .stream()
                .map(SimulateSnapshot::getCurrentPosition)
                .map(Position::getCoordinateTitleList)
                .findAny()
                .orElse(new ArrayList<>());
        titleList.add("值:");
        this.positionTitle = titleList;
        this.taskDetailList = simulateSnapshotList.stream()
                .map(TaskDetail::new)
                .collect(Collectors.toList());

    }
}
