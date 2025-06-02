package org.esimulate.core.pso.particle;

import lombok.Data;
import org.esimulate.core.pojo.pso.SimulateSnapshot;
import org.esimulate.core.pojo.simulate.PsoConfig;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.simulator.Simulator;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Data
public class Particle2 {

    //用于仿真的对象
    private List<EnvironmentData> environmentDataList;

    private List<LoadData> loadDataList;

    private List<Device> deviceList;

    // 当前位置
    private Position2 currentPosition;

    // 当前适应度值
    private BigDecimal fitnessValue;


    // 粒子的历史最优位置
    private Position2 bestPosition;

    //历史最优适应度值
    private BigDecimal bestFitnessValue;

    // 粒子的速度
    private Velocity2 velocity;

    // 惯性权重
    BigDecimal inertiaWeight;

    // 自我学习因子
    BigDecimal c1;

    // 群体学习因子
    BigDecimal c2;

    public Particle2(PsoConfig psoConfig, List<LoadData> loadDataList, List<EnvironmentData> environmentDataList, List<Device> deviceList) {
        this.loadDataList = loadDataList;
        this.environmentDataList = environmentDataList;
        this.deviceList = deviceList.stream()
                .map(Device::clone)
                .sorted(Comparator.comparing(Device::getSortKey))
                .collect(Collectors.toList());

        //把设备里面是纬度的模型挑出来
        List<Dimension> dimensionList = this.deviceList.stream()
                .filter(x -> x instanceof Dimension)
                .map(x -> (Dimension) x)
                .collect(Collectors.toList());

        //初始位置在所有纬度上都是 最低值
        this.currentPosition = new Position2(dimensionList);
        this.velocity = new Velocity2(new Integer[dimensionList.size()]);

        Random random = new Random();
        for (int i = 0; i < velocity.getDimensionCount(); i++) {
            velocity.getVelocities()[i] = random.nextInt(dimensionList.get(i).getUpperBound());
        }
    }

    public void move(Position2 globalBestPosition) {
        Random random = new Random();
        BigDecimal r1 = BigDecimal.valueOf(random.nextInt());
        BigDecimal r2 = BigDecimal.valueOf(random.nextInt());
        for (int i = 0; i < velocity.getDimensionCount(); i++) {
            Integer newVelocity = (inertiaWeight.multiply(BigDecimal.valueOf(this.velocity.getVelocityAt(i)))
                    .add(this.c1.multiply(r1).multiply(BigDecimal.valueOf(bestPosition.getValueAt(i)-(currentPosition.getValueAt(i)))))
                    .add(this.c2.multiply(r2).multiply(BigDecimal.valueOf(globalBestPosition.getValueAt(i)-(currentPosition.getValueAt(i))))))
                    .intValue();
            this.velocity.setAtDimension(i, newVelocity);
            this.currentPosition.setAtDimension(i, this.currentPosition.getValueAt(i) + this.velocity.getVelocityAt(i));
        }
    }

    public SimulateSnapshot runSimulate() {
        List<Device> currentSimulateDeviceList = deviceList.stream().map(Device::clone).collect(Collectors.toList());

        for (int i = 0; i < currentSimulateDeviceList.size(); i++) {
            currentSimulateDeviceList.get(i).setQuantity(BigDecimal.valueOf(this.currentPosition.getValueAt(i)));
        }

        SimulateResult simulateResult = Simulator.simulate(loadDataList, environmentDataList, currentSimulateDeviceList);
        return new SimulateSnapshot(currentPosition, fitnessValue, simulateResult);
    }
}
