package org.esimulate.core.pojo.pso;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.esimulate.core.model.result.indication.CurtailmentRate;
import org.esimulate.core.model.result.indication.RenewableEnergyShare;
import org.esimulate.core.pojo.simulate.result.SimulateResult;
import org.esimulate.core.pso.particle.Position;
import org.esimulate.core.pso.simulator.facade.result.indication.Indication;

import java.math.BigDecimal;
import java.math.RoundingMode;


@NoArgsConstructor
@Data
public class SimulateSnapshot {

    Integer particleIndex;

    Position currentPosition;

    BigDecimal fitnessValue;

    @JSONField(serialize = false)
    @Getter(AccessLevel.PRIVATE)
    SimulateResult simulateResult;

    Boolean isValid;

    String message;

    public SimulateSnapshot(Integer particleIndex, BigDecimal maxCurtailmentRate, BigDecimal minRenewableEnergyShare, Position currentPosition, BigDecimal fitnessValue, SimulateResult simulateResult) {
        this.particleIndex = particleIndex;
        this.currentPosition = currentPosition;
        this.fitnessValue = fitnessValue;
        this.simulateResult = simulateResult;
        this.isValid = evaluateFitnessValue(simulateResult, maxCurtailmentRate, minRenewableEnergyShare);

    }

    private Boolean evaluateFitnessValue(SimulateResult simulateResult, BigDecimal maxCurtailmentRate, BigDecimal minRenewableEnergyShare) {
        BigDecimal renewableEnergyShare = simulateResult.getIndicationList().stream()
                .filter(x -> x instanceof RenewableEnergyShare)
                .map(Indication::getIndication)
                .findAny()
                .orElse(BigDecimal.ZERO);

        BigDecimal curtailmentRate = simulateResult.getIndicationList().stream()
                .filter(x -> x instanceof CurtailmentRate)
                .map(Indication::getIndication)
                .findAny()
                .orElse(BigDecimal.ZERO);

        if (renewableEnergyShare.compareTo(minRenewableEnergyShare) < 0) {
            this.message = String.format("不满足约束: 可再生能源渗透率不足%.2f%%", minRenewableEnergyShare.setScale(2, RoundingMode.HALF_UP));
            return false;
        }

        if (curtailmentRate.compareTo(maxCurtailmentRate) > 0) {
            this.message = String.format("不满足约束: 弃风弃光率超过%.2f%%", maxCurtailmentRate.setScale(2, RoundingMode.HALF_UP));
            return false;
        }

        return true;
    }

}
