package org.core.pso.simulator;

import org.core.pso.simulator.facade.base.TimeSeriesData;
import org.core.pso.simulator.facade.environment.EnvironmentData;
import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.load.LoadData;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.Provider;
import org.core.pso.simulator.facade.Storage;
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
                                          List<Provider> providerList) {

        int timeLength = validateDataLengthAndGetDataLength(loadList, environmentList);

        for (int timeIndex = 0; timeIndex < timeLength; timeIndex++) {
            final Integer finalTimeIndex = timeIndex;

            List<EnvironmentValue> environmentListAtAMount = environmentList.stream()
                    .map(x -> x.getEnvironmentValue(finalTimeIndex))
                    .collect(Collectors.toList());

            List<BigDecimal> produceList = producerList.stream()
                    .map(x -> x.produce(environmentListAtAMount))
                    .collect(Collectors.toList());



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

