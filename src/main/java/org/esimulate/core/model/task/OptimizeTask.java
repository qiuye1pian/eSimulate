package org.esimulate.core.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.enums.TaskStateEnum;
import org.esimulate.core.pojo.pso.OptimizeResult;
import org.esimulate.core.pojo.simulate.PsoConfig;

import javax.persistence.*;
import javax.persistence.Convert;
import org.esimulate.core.converter.PsoConfigConverter;
import org.esimulate.core.converter.OptimizeResultConverter;
import java.sql.Timestamp;

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
    @Convert(converter = OptimizeResultConverter.class)
    @Column(name = "optimize_result", columnDefinition = "TEXT")
    private OptimizeResult optimizeResult;

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
}
