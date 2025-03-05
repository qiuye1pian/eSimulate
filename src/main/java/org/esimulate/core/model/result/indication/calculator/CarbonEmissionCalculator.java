package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.CarbonEmission;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.Provider;
import org.esimulate.core.pso.simulator.facade.Storage;
import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarbonEmissionCalculator {

    public static Indication calculate(List<Producer> producerList,
                                       List<Storage> storageList,
                                       List<Provider> providerList) {

        //将 producerList,storageList, providerList 三个列表 合并成同一个列表 List<CarbonEmitter>
        List<CarbonEmitter> combinedList = new ArrayList<>();
        combinedList.addAll(producerList);
        combinedList.addAll(storageList);
        combinedList.addAll(providerList);

        //这个新列表map -> calculateCarbonEmissions 计算出总的碳排放量
        BigDecimal totalCarbonEmission = combinedList.stream()
                .map(CarbonEmitter::calculateCarbonEmissions)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        //返回总碳排放量
        return new CarbonEmission(totalCarbonEmission);
    }
}
