package org.core.pso.simulator;

import org.core.model.result.MomentResult;
import org.core.model.result.indication.CarbonEmission;
import org.core.model.result.indication.calculator.CarbonEmissionCalculator;
import org.core.model.result.indication.calculator.RenewableEnergyShareCalculator;
import org.core.pso.simulator.facade.Producer;
import org.core.pso.simulator.facade.Provider;
import org.core.pso.simulator.facade.Storage;
import org.core.pso.simulator.facade.base.TimeSeriesData;
import org.core.pso.simulator.facade.constraint.Constraint;
import org.core.pso.simulator.facade.environment.EnvironmentData;
import org.core.pso.simulator.facade.environment.EnvironmentValue;
import org.core.pso.simulator.facade.load.LoadData;
import org.core.pso.simulator.facade.result.MomentResultFacade;
import org.core.pso.simulator.facade.result.energy.Energy;
import org.core.pso.simulator.facade.result.indication.Indication;
import org.core.pso.simulator.result.SimulateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Simulator {

    List<MomentResultFacade> momentResultList = new ArrayList<>();

    private static MomentResultFacade calculateAMoment(List<LoadData> loadList, List<EnvironmentData> environmentList, List<Producer> producerList, List<Storage> storageList, List<Provider> providerList, int timeIndex) {
        final Integer currentTimeIndex = timeIndex;

        //准备好环境数据
        List<EnvironmentValue> environmentListAtAMount = environmentList.stream()
                .map(x -> x.getEnvironmentValue(currentTimeIndex))
                .collect(Collectors.toList());

        //将环境数据扔给模型，各模型根据环境数据，生产能源
        // 返回的是不同能量的产出值
        // 列表里根据仿真参与的模型，结果可能混合了电能和热能
        List<Energy> produceList = producerList.stream()
                .map(x -> x.produce(environmentListAtAMount))
                .collect(Collectors.toList());

        //用负荷数据减去已生产的能源，电能和热能分开计算的，获得能源 冗余/缺口 数据
        List<Energy> differenceList = loadList.stream()
                // 当前时刻的负荷
                .map(x -> x.getLoadValue(currentTimeIndex))
                // 分别去计算能量冗余/缺口
                // 冗余/缺口 数据 = 当前时刻能量产出量 - 当前时刻的负荷
                .map(x -> x.calculateDifference(produceList))
                //返回的数是正的代表产出值大于负荷
                .collect(Collectors.toList());

        //计算经过储能调整后的 冗余/缺口 数据
        List<Energy> afterStorageEnergyList = storageList.stream()
                //热能和电能分开计算
                .map(x -> x.storage(differenceList))
                //通过储能计算后，各能源的 冗余/缺口
                .collect(Collectors.toList());

        //供应商作为兜底，将 调整后的 冗余/缺口 数据 交给供应商作为最后补充
        List<Energy> afterProvideList = providerList.stream()
                .map(x -> x.provide(afterStorageEnergyList))
                .collect(Collectors.toList());

        return new MomentResult(afterProvideList);
    }

    @SafeVarargs
    private static Integer validateDataLengthAndGetDataLength(List<? extends TimeSeriesData>... dataLists) {

        final List<Integer> distinct = Stream.of(dataLists).flatMap(List::stream).map(TimeSeriesData::getDataLength).distinct().collect(Collectors.toList());

        boolean isLengthMismatch = distinct.size() > 1;

        if (isLengthMismatch) {
            throw new RuntimeException("环境数据、负荷数据长度不一致");
        }
        return distinct.stream().findAny().orElse(-1);
    }

    /**
     * @param loadList        负荷
     * @param environmentList 环境数据
     * @param producerList    生产者
     * @param storageList     储能
     * @param providerList    供应商
     * @param constraintList  约束
     * @return 仿真结果
     */
    public SimulateResult simulate(List<LoadData> loadList, List<EnvironmentData> environmentList,
                                   List<Producer> producerList, List<Storage> storageList,
                                   List<Provider> providerList, List<Constraint> constraintList) {

        //验证负荷长度和环境长度是否一致，如果一致则返回他们的长度
        int timeLength = validateDataLengthAndGetDataLength(loadList, environmentList);

        //计算某一时刻的情景
        momentResultList = IntStream.range(0, timeLength)
                .mapToObj(timeIndex ->
                        calculateAMoment(loadList, environmentList, producerList, storageList, providerList, timeIndex))
                .collect(Collectors.toList());

        //计算指标
        // 例如

        //校验仿真约束
        //校验是否 100% 满足负荷
        if (momentResultList.stream().anyMatch(MomentResultFacade::isUnqualified)) {
            return SimulateResult.fail("不能满足负荷");
        }


        Indication renewableEnergyPercent = RenewableEnergyShareCalculator.calculate(producerList, providerList);
        Indication carbonEmission = CarbonEmissionCalculator.calculate(producerList, storageList, providerList);


        //TODO:需要整理
        return SimulateResult.success();


    }

}

