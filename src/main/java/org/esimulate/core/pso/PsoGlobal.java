package org.esimulate.core.pso;

import lombok.Data;
import org.esimulate.core.pso.particle.Particle2;
import org.esimulate.core.pso.particle.Position;
import org.esimulate.core.pso.simulator.facade.Device;

import java.util.List;

@Data
public class PsoGlobal {

    private List<Particle2> particleList;

    private List<Device> deviceList;

    private Position globalBestPosition;



}
