package org.esimulate.core.pso.particle;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.result.indication.TotalCost;
import org.esimulate.core.pojo.pso.SimulateSnapshot;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.simulator.Simulator;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Particle {

    // 惯性权重 初值
    BigDecimal inertiaWeightStart;

    // 惯性权重 终值
    BigDecimal inertiaWeightEnd;

    // 自我学习因子 初值
    BigDecimal c1Start;

    // 自我学习因子 终值
    BigDecimal c1End;

    // 群体学习因子 初值
    BigDecimal c2Start;

    // 群体学习因子 终值
    BigDecimal c2End;

    // 最大迭代次数
    Integer maxIterations;

    Integer currentIterations;

    //用于仿真的对象
    private List<EnvironmentData> environmentDataList;

    private List<LoadData> loadDataList;

    private List<Device> deviceList;

    // 当前位置
    private Position currentPosition;

    // 当前适应度值
    private BigDecimal fitnessValue;

    // 粒子的历史最优位置
    private Position bestPosition;

    //历史最优适应度值
    private BigDecimal bestFitnessValue;

    // 粒子的速度
    private Velocity velocity;

    public Particle(PsoConfig psoConfig, List<LoadData> loadDataList, List<EnvironmentData> environmentDataList, List<Device> deviceList) {
        this.loadDataList = loadDataList;
        this.environmentDataList = environmentDataList;
        this.deviceList = deviceList.stream()
                .map(Device::clone)
                .sorted(Comparator.comparing(Device::getSortKey))
                .collect(Collectors.toList());

        this.inertiaWeightStart = psoConfig.getInertiaWeightStart();
        this.inertiaWeightEnd = psoConfig.getInertiaWeightEnd();
        this.c1Start = psoConfig.getC1Start();
        this.c1End = psoConfig.getC1End();
        this.c2Start = psoConfig.getC2Start();
        this.c2End = psoConfig.getC2End();
        this.maxIterations = psoConfig.getMaxIterations();
        this.currentIterations = 0;

        //把设备里面是纬度的模型挑出来
        List<Dimension> dimensionList = this.deviceList.stream()
                .filter(x -> x instanceof Dimension)
                .map(x -> (Dimension) x)
                .collect(Collectors.toList());

        //初始位置在所有纬度上都是 最低值
        this.currentPosition = new Position(dimensionList);
        this.velocity = new Velocity(new Integer[dimensionList.size()]);
        this.bestPosition = currentPosition;
        this.bestFitnessValue = BigDecimal.valueOf(Double.MAX_VALUE);

        Random random = new Random();
        for (int i = 0; i < velocity.getDimensionCount(); i++) {
            velocity.getVelocities()[i] = random.nextInt(dimensionList.get(i).getUpperBound());
        }

        log.info("Init:{}", bestFitnessValue);
        log.info("Position:{}", currentPosition);
        log.info("velocity:{}", velocity);
    }

    public void move(Position globalBestPosition) {
        if (globalBestPosition == null) {
            globalBestPosition = currentPosition;
        }
        Random random = new Random();
        for (int i = 0; i < velocity.getDimensionCount(); i++) {
            BigDecimal r1 = BigDecimal.valueOf(random.nextDouble());
            BigDecimal r2 = BigDecimal.valueOf(random.nextDouble());
            Integer newVelocity = (this.getInertiaWeight().multiply(BigDecimal.valueOf(this.velocity.getVelocityAt(i)))
                    .add(this.getC1().multiply(r1).multiply(BigDecimal.valueOf(bestPosition.getValueAt(i) - currentPosition.getValueAt(i))))
                    .add(this.getC2().multiply(r2).multiply(BigDecimal.valueOf(globalBestPosition.getValueAt(i) - currentPosition.getValueAt(i)))))
                    .intValue();
            this.velocity.setAtDimension(i, newVelocity);
            this.currentPosition.setAtDimension(i, this.currentPosition.getValueAt(i) + this.velocity.getVelocityAt(i));
        }
        log.info("Moved");
        log.info("Position list:{}", currentPosition.getCoordinateValueList());
        log.info("GlobalBestPosition list:{}", globalBestPosition.getCoordinateValueList());
        log.info("velocity list:{}", velocity);
    }

    private BigDecimal getInertiaWeight() {
        return linearInterpolate(this.inertiaWeightStart, this.inertiaWeightEnd);
    }

    private BigDecimal getC1() {
        return linearInterpolate(this.c1Start, this.c1End);
    }

    private BigDecimal getC2() {
        return linearInterpolate(this.c2Start, this.c2End);
    }

    /**
     * 线性插值：根据当前迭代次数与最大迭代次数计算参数从 start 到 end 的线性衰减值
     */
    private BigDecimal linearInterpolate(BigDecimal start, BigDecimal end) {
        BigDecimal ratio = BigDecimal.valueOf(this.currentIterations)
                .divide(BigDecimal.valueOf(this.maxIterations), 10, RoundingMode.HALF_UP);
        return start.subtract(start.subtract(end).multiply(ratio));
    }

    public SimulateSnapshot runSimulate() {
        List<Device> currentSimulateDeviceList = deviceList.stream().map(Device::clone).collect(Collectors.toList());

        AtomicInteger i = new AtomicInteger(0);
        deviceList.stream()
                .filter(x -> x instanceof Dimension)
                .forEach(x -> x.setQuantity(BigDecimal.valueOf(this.currentPosition.getValueAt(i.getAndIncrement()))));

        SimulateResult simulateResult = Simulator.simulate(loadDataList, environmentDataList, currentSimulateDeviceList);
        fitnessValue = simulateResult.getIndicationList().stream()
                .filter(x->x instanceof TotalCost)
                .findAny()
                .map(Indication::getIndication)
                .orElse(BigDecimal.valueOf(Double.MAX_VALUE));
        if (this.bestFitnessValue.compareTo(fitnessValue) >= 0) {
            log.info("bestFitnessValue changed:{}->{}", bestFitnessValue, fitnessValue);
            this.bestFitnessValue = fitnessValue;
            this.bestPosition = currentPosition;
        }
        return new SimulateSnapshot(currentPosition, fitnessValue, simulateResult);
    }
}
