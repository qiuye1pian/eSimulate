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
import org.jetbrains.annotations.NotNull;

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

    private Integer particleIndex;

    // 惯性权重 初值
    private BigDecimal inertiaWeightStart;

    // 惯性权重 终值
    private BigDecimal inertiaWeightEnd;

    // 自我学习因子 初值
    private BigDecimal c1Start;

    // 自我学习因子 终值
    private BigDecimal c1End;

    // 群体学习因子 初值
    private BigDecimal c2Start;

    // 群体学习因子 终值
    private BigDecimal c2End;

    // 最大迭代次数
    private Integer maxIterations;

    // 最大弃风弃光率
    private BigDecimal maxCurtailmentRate;

    // 最小可再生能源渗透率
    private BigDecimal minRenewableEnergyShare;

    private Integer currentIterations;

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

    public Particle(Integer particleIndex, PsoConfig psoConfig, List<LoadData> loadDataList, List<EnvironmentData> environmentDataList, List<Device> deviceList) {
        this.particleIndex = particleIndex;
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
        this.maxCurtailmentRate = psoConfig.getMaxCurtailmentRate();
        this.minRenewableEnergyShare = psoConfig.getMinRenewableEnergyShare();

        //把设备里面是纬度的模型挑出来
        List<Dimension> dimensionList = this.deviceList.stream()
                .filter(x -> x instanceof Dimension)
                .map(x -> (Dimension) x)
                .collect(Collectors.toList());

        //初始位置在所有纬度上都是 最低值
        this.currentPosition = new Position(dimensionList);
        this.velocity = new Velocity(new Integer[dimensionList.size()]);
        this.bestPosition = currentPosition.clone();
        this.bestFitnessValue = new BigDecimal("99999999999999999.99");

        Random random = new Random();
        for (int i = 0; i < velocity.getDimensionCount(); i++) {
            velocity.getVelocities()[i] = random.nextInt(Math.max(1, dimensionList.get(i).getUpperBound() / 100));
        }

    }

    public void move(Position globalBestPosition) {
        Position newPosition = currentPosition.clone();
        Velocity newVelocity = velocity.clone();
        Random random = new Random();
        for (int i = 0; i < velocity.getDimensionCount(); i++) {
            BigDecimal r1 = BigDecimal.valueOf(random.nextDouble());
            BigDecimal r2 = BigDecimal.valueOf(random.nextDouble());

            int distanceFromParticleBest = bestPosition.getValueAt(i) - currentPosition.getValueAt(i);
            int distanceFromGlobalBest = globalBestPosition.getValueAt(i) - currentPosition.getValueAt(i);

            BigDecimal lastVelocityFactor = this.getInertiaWeight().multiply(BigDecimal.valueOf(this.velocity.getVelocityAt(i)));
            BigDecimal factor1 = this.getC1().multiply(r1).multiply(BigDecimal.valueOf(distanceFromParticleBest));
            BigDecimal factor2 = this.getC2().multiply(r2).multiply(BigDecimal.valueOf(distanceFromGlobalBest));

            Integer newDimensionVelocity = (lastVelocityFactor.add(factor1).add(factor2)).intValue();

            // 原始计算的新位置
            int rawValue = this.currentPosition.getValueAt(i) + newDimensionVelocity;
            // 获取当前维度的边界
            int lowerBound = this.currentPosition.getLowerBoundAt(i);
            int upperBound = this.currentPosition.getUpperBoundAt(i);

            // 裁边：如果超出上下限，就修正到边界
            int boundedValue = Math.max(lowerBound, Math.min(rawValue, upperBound));

            // 调整速度，与裁边后的位置一致
            int adjustedVelocity = boundedValue - this.currentPosition.getValueAt(i);

            newVelocity.setAtDimension(i, adjustedVelocity);
            newPosition.setAtDimension(i, boundedValue);

        }

        log.debug("[Particle {}] ==============>\tStep {} Start\t===⬇️⬇️⬇️⬇️", this.particleIndex, this.currentIterations);
//        log.info("[Particle {}] ==============>\tMoving\t==============", this.particleIndex);
//        log.info("[Particle {}] ==\tOld Position:  \t{}", this.particleIndex, currentPosition.getCoordinateValueList());
//        log.info("[Particle {}] ==\tVelocity:      \t{}", this.particleIndex, (Object) newVelocity.getVelocities());
//        log.info("[Particle {}] ==\tNew Position:  \t{}", this.particleIndex, newPosition.getCoordinateValueList());
//        log.info("[Particle {}] ==============>\tMoved\t==============", this.particleIndex);
        this.currentPosition = newPosition;
        this.velocity = newVelocity;
        this.currentIterations++;
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

        this.fitnessValue = evaluateFitnessValue(simulateResult);

        if (this.bestFitnessValue.compareTo(this.fitnessValue) > 0) {
            log.info("bestFitnessValue changed:{}->{}", this.bestFitnessValue, this.fitnessValue);
            this.bestFitnessValue = this.fitnessValue;
            this.bestPosition = this.currentPosition.clone();
        }
        log.debug("[Particle {}] =====>Simulate finish<=====", this.particleIndex);
        log.debug("[Particle {}] Position:{}\tvalue:{}", this.particleIndex, this.currentPosition.getCoordinateValueList(), this.fitnessValue);
        log.debug("[Particle {}] BestPosition:{}\tbestValue:{}", this.particleIndex, this.bestPosition.getCoordinateValueList(), this.bestFitnessValue);
        log.debug("[Particle {}] ==============>\tStep {} End=======\t⬆️⬆️⬆️⬆️", this.particleIndex, this.currentIterations);

        return new SimulateSnapshot(this.particleIndex, this.maxCurtailmentRate, this.minRenewableEnergyShare, this.currentPosition, this.fitnessValue, simulateResult);
    }

    private @NotNull BigDecimal evaluateFitnessValue(SimulateResult simulateResult) {
        return simulateResult.getIndicationList().stream()
                .filter(x -> x instanceof TotalCost)
                .findAny()
                .map(Indication::getIndication)
                .orElse(new BigDecimal("99999999999999999.99"));

    }
}
