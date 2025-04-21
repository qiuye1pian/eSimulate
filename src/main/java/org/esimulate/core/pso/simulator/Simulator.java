package org.esimulate.core.pso.simulator;

import lombok.extern.slf4j.Slf4j;
import org.esimulate.core.model.load.electric.ElectricLoadData;
import org.esimulate.core.model.result.MomentResult;
import org.esimulate.core.model.result.indication.calculator.CarbonEmissionCalculator;
import org.esimulate.core.model.result.indication.calculator.CurtailmentRateCalculator;
import org.esimulate.core.model.result.indication.calculator.RenewableEnergyShareCalculator;
import org.esimulate.core.model.result.indication.calculator.TotalCostCalculator;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pojo.simulate.result.SimulateResultType;
import org.esimulate.core.pojo.simulate.result.StackedChartData;
import org.esimulate.core.pojo.simulate.result.StackedChartDto;
import org.esimulate.core.pso.simulator.facade.*;
import org.esimulate.core.pso.simulator.facade.base.TimeSeriesData;
import org.esimulate.core.pso.simulator.facade.constraint.Constraint;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentData;
import org.esimulate.core.pso.simulator.facade.environment.EnvironmentValue;
import org.esimulate.core.pso.simulator.facade.load.LoadData;
import org.esimulate.core.pso.simulator.facade.load.LoadValue;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;
import org.esimulate.util.DateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class Simulator {


    /**
     * @param loadList        负荷
     * @param environmentList 环境数据
     * @param deviceList      模型列表
     * @param constraintList  约束
     * @return 仿真结果
     */
    public static SimulateResult simulate(List<LoadData> loadList, List<EnvironmentData> environmentList,
                                          List<Device> deviceList, List<Constraint> constraintList) {
        try {
            List<Producer> producerList = deviceList.stream()
                    .filter(x -> x instanceof Producer)
                    .map(x -> (Producer) x)
                    .collect(Collectors.toList());

            List<Storage> storageList = deviceList.stream()
                    .filter(x -> x instanceof Storage)
                    .map(x -> (Storage) x)
                    .collect(Collectors.toList());

            List<Provider> providerList = deviceList.stream()
                    .filter(x -> x instanceof Provider)
                    .map(x -> (Provider) x)
                    .collect(Collectors.toList());

            //验证负荷长度和环境长度是否一致，如果一致则返回他们的长度
            int timeLength = validateDataLengthAndGetDataLength(loadList, environmentList);

            //计算某一时刻的情景
            List<MomentResult> momentResultList = IntStream.range(0, timeLength)
                    .mapToObj(timeIndex ->
                            calculateAMoment(loadList, environmentList, producerList, storageList, providerList, timeIndex))
                    .collect(Collectors.toList());

            return SimulateResult.builder()
                    .loadList(loadList)
                    .producerList(producerList)
                    .storageList(storageList)
                    .providerList(providerList)
                    .momentResultList(momentResultList)
                    .indicationList(getIndications(producerList, providerList, storageList, momentResultList))
                    .electricStackedChartDto(getElectricStackedChartDto(loadList, deviceList))
                    .thermalStackedChartDto(getThermalStackedChartDto(loadList, deviceList))
                    .resultType(SimulateResultType.SUCCESS)
                    .build();

        } catch (Exception ex) {
            log.info("仿真失败", ex);
            return SimulateResult.fail(ex.getMessage());
        }

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

    private static MomentResult calculateAMoment(List<LoadData> loadList,
                                                 List<EnvironmentData> environmentList,
                                                 List<Producer> producerList,
                                                 List<Storage> storageList,
                                                 List<Provider> providerList,
                                                 int timeIndex) {

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
                // 用能量类型来分类
                .collect(Collectors.toMap(
                        LoadValue::getClass,
                        e -> e,
                        // 求和
                        LoadValue::add
                )).values()
                .stream()
                // 分别去计算能量冗余/缺口
                // 冗余/缺口 数据 = 当前时刻能量产出量 - 当前时刻的负荷
                .map(x -> x.calculateDifference(produceList))
                //返回的数是正的代表产出值大于负荷
                .collect(Collectors.toList());

        //如果有储能设备， 计算经过储能调整后的 冗余/缺口 数据
        List<Energy> afterStorageEnergyList = CollectionUtils.isEmpty(storageList) ? differenceList : storageList.stream()
                //热能和电能分开计算
                .map(x -> x.storage(differenceList))
                //通过储能计算后，各能源的 冗余/缺口
                .collect(Collectors.toList());

        //供应商作为兜底，将 调整后的 冗余/缺口 数据 交给供应商作为最后补充
        List<Energy> afterProvideList = providerList.stream()
                .map(x -> x.provide(afterStorageEnergyList))
                .collect(Collectors.toList());

        //剩余的能源将被丢弃，电能为弃风弃光，热能为自然散逸
        return new MomentResult(afterProvideList);
    }

    private static @NotNull StackedChartDto getElectricStackedChartDto(List<LoadData> loadList, List<Device> deviceList) {
        List<StackedChartData> deviceStackedChartDataList = deviceList.stream()
                .filter(x -> x instanceof ElectricDevice)
                .map(x -> (ElectricDevice) x)
                .map(ElectricDevice::getStackedChartDataList)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<String> sortedLocalDateTimes = loadList.stream()
                .filter(x -> x instanceof ElectricLoadData)
                .findFirst()
                .map(LoadData::getDatetimeList)
                .map(list -> list.stream()
                        .sorted()
                        .map(DateTimeUtil::formatNoYearString)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        List<StackedChartData> loadStackedChartDataList = loadList.stream()
                .filter(x -> x instanceof ElectricLoadData)
                .map(x -> new StackedChartData(x.getLoadName(), x.getLoadValueList(), 200))
                .collect(Collectors.toList());

        List<StackedChartData> mergedStackedChartDataList = new java.util.ArrayList<>();
        mergedStackedChartDataList.addAll(deviceStackedChartDataList);
        mergedStackedChartDataList.addAll(loadStackedChartDataList);

        return new StackedChartDto(sortedLocalDateTimes, mergedStackedChartDataList);
    }

    private static @NotNull StackedChartDto getThermalStackedChartDto(List<LoadData> loadList, List<Device> deviceList) {
        List<StackedChartData> deviceStackedChartDataList = deviceList.stream()
                .filter(x -> x instanceof ThermalDevice)
                .map(x -> (ThermalDevice) x)
                .map(ThermalDevice::getStackedChartDataList)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<String> sortedLocalDateTimes = loadList.stream()
                .findFirst()
                .map(LoadData::getDatetimeList)
                .map(list -> list.stream()
                        .sorted()
                        .map(DateTimeUtil::formatNoYearString)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        List<StackedChartData> loadStackedChartDataList = loadList.stream()
                .filter(x -> x instanceof ThermalDevice)
                .map(x -> new StackedChartData(x.getLoadName(), x.getLoadValueList(), 200))
                .collect(Collectors.toList());

        List<StackedChartData> mergedStackedChartDataList = new java.util.ArrayList<>();
        mergedStackedChartDataList.addAll(deviceStackedChartDataList);
        mergedStackedChartDataList.addAll(loadStackedChartDataList);

        return new StackedChartDto(sortedLocalDateTimes, mergedStackedChartDataList);
    }

    private static @NotNull List<Indication> getIndications(List<Producer> producerList, List<Provider> providerList, List<Storage> storageList, List<MomentResult> momentResultList) {
        Indication renewableEnergyPercent = RenewableEnergyShareCalculator.calculate(producerList, providerList);
        Indication carbonEmission = CarbonEmissionCalculator.calculate(producerList, storageList, providerList);
        Indication totalCost = TotalCostCalculator.calculate(producerList, storageList, providerList);
        Indication curtailmentRate  = CurtailmentRateCalculator.calculate(producerList, momentResultList);
        return Arrays.asList(renewableEnergyPercent, carbonEmission, totalCost, curtailmentRate);
    }

}
