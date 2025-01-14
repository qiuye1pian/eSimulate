package org.core.pso.simulator;

import org.core.pso.simulator.facade.base.TimeSeriesData;
import org.core.pso.simulator.facade.constraint.Constraint;
import org.core.pso.simulator.facade.environment.EnvironmentData;
import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.load.LoadData;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.Provider;
import org.core.pso.simulator.facade.Storage;
import org.core.pso.simulator.facade.result.energy.Energy;
import org.core.pso.simulator.result.SimulateResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Simulator {


    public static SimulateResult simulate(List<LoadData> loadList,
                                          List<EnvironmentData> environmentList,
                                          List<Producer> producerList,
                                          List<Storage> storageList,
                                          List<Provider> providerList,
                                          List<Constraint>  constraintList) {

        int timeLength = validateDataLengthAndGetDataLength(loadList, environmentList);

        for (int timeIndex = 0; timeIndex < timeLength; timeIndex++) {

            final Integer currentTimeIndex = timeIndex;

            //准备好环境数据
            List<EnvironmentValue> environmentListAtAMount = environmentList.stream()
                    .map(x -> x.getEnvironmentValue(currentTimeIndex))
                    .collect(Collectors.toList());

            //将环境数据扔给模型，各模型根据环境数据，生产能源
            List<Energy> produceList = producerList.stream()
                    .map(x -> x.produce(environmentListAtAMount))
                    .collect(Collectors.toList());

            //用负荷数据减去已生产的能源，获得能源 冗余/缺口 数据
            List<Energy> differenceList = loadList.stream()
                    .map(x->x.getLoadValue(currentTimeIndex))
                    .map(x->x.calculateDifference(produceList))
                    .collect(Collectors.toList());


            //计算经过储能调整后的 冗余/缺口 数据
            List<Energy> afterStorageEnergyList = storageList.stream()
                    .map(x -> x.storage(differenceList))
                    .collect(Collectors.toList());

            //供应商作为兜底，将 调整后的 冗余/缺口 数据 交给供应商作为最后补充
            List<Energy> provideList = providerList.stream()
                    .map(x -> x.provide(afterStorageEnergyList))
                    .collect(Collectors.toList());

            //校验仿真约束


        }


        return null;
    }

    @SafeVarargs
    private static Integer validateDataLengthAndGetDataLength(List<? extends TimeSeriesData>... dataLists) {

        final List<Integer> distinct = Stream.of(dataLists)
                .flatMap(List::stream)
                .map(TimeSeriesData::getDataLength)
                .distinct()
                .collect(Collectors.toList());

        boolean isLengthMismatch = distinct.size() > 1;

        if (isLengthMismatch) {
            throw new RuntimeException("环境数据、负荷数据长度不一致");
        }
        return distinct.stream().findAny().orElse(-1);
    }

}

