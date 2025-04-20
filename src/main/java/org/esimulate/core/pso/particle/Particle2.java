package org.esimulate.core.pso.particle;

import lombok.Data;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.load.LoadData;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Particle2 {

    //用于仿真的对象
    List<EnvironmentData> environmentDataList;

    List<LoadData> loadDataList;

    List<Device> deviceList;

    Position currentPosition;

    public Particle2(List<LoadData> loadDataList, List<EnvironmentData> environmentDataList, List<Device> deviceList) {
        this.loadDataList = loadDataList;
        this.environmentDataList = environmentDataList;
        this.deviceList = deviceList;
        //把设备里面是纬度的模型挑出来
        List<Dimension> dimensionList = deviceList.stream()
                .filter(x -> x instanceof Dimension)
                .map(x -> (Dimension) x)
                .collect(Collectors.toList());
        //初始位置在所有纬度上都是1
        this.currentPosition = new Position(dimensionList);

    }


}
