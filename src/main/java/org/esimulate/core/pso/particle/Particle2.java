package org.esimulate.core.pso.particle;

import lombok.Data;
import org.esimulate.core.pojo.pso.SimulateSnapshot;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.simulator.Simulator;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Particle2 {

    //用于仿真的对象
    private List<EnvironmentData> environmentDataList;

    private List<LoadData> loadDataList;

    private List<Device> deviceList;

    private Position currentPosition;

    private BigDecimal fitnessValue;

    // 粒子的速度
    // todo 速度需要初始化
    private Velocity velocity;


    public Particle2(List<LoadData> loadDataList, List<EnvironmentData> environmentDataList, List<Device> deviceList) {
        this.loadDataList = loadDataList;
        this.environmentDataList = environmentDataList;
        this.deviceList = deviceList;

        //把设备里面是纬度的模型挑出来
        List<Dimension> dimensionList = deviceList.stream()
                .filter(x -> x instanceof Dimension)
                .map(x -> (Dimension) x)
                .collect(Collectors.toList());

        //初始位置在所有纬度上都是 最低值
        this.currentPosition = new Position(dimensionList);

    }

    public void move() {

    }

    public SimulateSnapshot runSimulate() {
        SimulateResult simulateResult = Simulator.simulate(loadDataList, environmentDataList, deviceList);
        return new SimulateSnapshot(currentPosition, fitnessValue, simulateResult);
    }
}
