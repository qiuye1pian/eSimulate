package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.result.indication.CarbonEmission;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.result.carbon.CarbonEmitter;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.util.List;

public class CarbonEmissionCalculator {

    public static Indication calculate(List<Device> deviceList) {

        //这个新列表map -> calculateCarbonEmissions 计算出总的碳排放量
        BigDecimal totalCarbonEmission = deviceList.stream()
                .filter(x -> x instanceof CarbonEmitter)
                .map(x -> (CarbonEmitter) x)
                .map(CarbonEmitter::calculateCarbonEmissions)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        //返回总碳排放量
        return new CarbonEmission(totalCarbonEmission);
    }
}
