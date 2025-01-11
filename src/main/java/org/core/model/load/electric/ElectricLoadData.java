package org.core.model.load.electric;

import org.core.pso.simulator.LoadData;

import java.math.BigDecimal;
import java.util.List;

public interface ElectricLoadData extends LoadData {
    List<BigDecimal> getElectricLoadData();
}
