package org.esimulate.core.model.result.indication.calculator;

import org.esimulate.core.model.device.SolarPowerModel;
import org.esimulate.core.model.device.WindPowerModel;
import org.esimulate.core.model.result.MomentResult;
import org.esimulate.core.model.result.energy.ElectricEnergy;
import org.esimulate.core.model.result.indication.CurtailmentRate;
import org.esimulate.core.pso.simulator.facade.Device;
import org.esimulate.core.pso.simulator.facade.Producer;
import org.esimulate.core.pso.simulator.facade.result.energy.Energy;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CurtailmentRateCalculator {

    public static Indication calculate(List<Producer> producerList, List<MomentResult> momentResultList) {

        BigDecimal totalWindAndSolarPower = producerList.stream()
                .filter(x -> x instanceof SolarPowerModel || x instanceof WindPowerModel)
                .map(x -> (Device) x)
                .map(x -> ((Producer) x).getTotalEnergy())
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        if (BigDecimal.ZERO.equals(totalWindAndSolarPower)) {
            return new CurtailmentRate(BigDecimal.valueOf(100));
        }

        BigDecimal curtailedEnergy = momentResultList.stream()
                .map(MomentResult::getCurtailmentEnergyList)
                .flatMap(List::stream)
                .filter(x -> x instanceof ElectricEnergy)
                .map(Energy::getValue)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        if (BigDecimal.ZERO.equals(curtailedEnergy)) {
            return new CurtailmentRate(BigDecimal.ZERO);
        }

        BigDecimal curtailmentRate = curtailedEnergy.divide(totalWindAndSolarPower, 2, RoundingMode.HALF_UP);

        if (curtailmentRate.compareTo(BigDecimal.ONE) > 0) {
            return new CurtailmentRate(BigDecimal.valueOf(100));
        }

        return new CurtailmentRate(curtailmentRate.multiply(BigDecimal.valueOf(100).setScale(0, RoundingMode.HALF_UP)));
    }
}
