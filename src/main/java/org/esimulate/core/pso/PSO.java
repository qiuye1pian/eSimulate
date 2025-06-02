package org.esimulate.core.pso;

import lombok.Getter;
import lombok.Setter;
import org.esimulate.core.pso.dto.PSOParameters;
import org.esimulate.core.pso.particle.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class PSO {
    private PSOParameters params;
    private List<Particle3> particle3List = new ArrayList<>();
    private Position3 globalBestPosition3;
    private BigDecimal globalBestFitness;


    public PSO(PSOParameters params) {
        this.params = params;
        this.globalBestPosition3 = new Position3(params.getDimensionList());
        this.globalBestFitness = BigDecimal.valueOf(Double.MAX_VALUE);
        initializeParticles();
    }

    public static void main(String[] args) {
        List<Dimension> dimensionList = new ArrayList<>();
        List<EnvironmentLoad> environmentLoadList = new ArrayList<>();
        PSOParameters psoParameters = new PSOParameters(dimensionList, environmentLoadList, 50, 200, 0.5, 1.5, 1.5);
        PSO pso = new PSO(psoParameters);
        pso.optimize();
    }

    // 初始化粒子
    private void initializeParticles() {
        for (int i = 0; i < params.getParticleCount(); i++) {
            particle3List.add(new Particle3(params.getDimensionList()));
        }
    }

    // 适应度函数（目标函数示例：Sphere Function）
    // TODO:这里要改成调用simulator
    private BigDecimal evaluateFitness(Position3 position3) {
        BigDecimal fitness = BigDecimal.ZERO;
        for (BigDecimal value : position3.getCoordinateValueList()) {
            fitness = fitness.add(value.multiply(value));
        }
        return fitness.setScale(10, RoundingMode.HALF_UP);
    }

    // 更新速度和位置
    public void updateVelocityAndPosition(Particle3 particle3) {

        Random random = new Random();
        Velocity3 newVelocity3 = new Velocity3(new BigDecimal[params.getDimensionCount()]);
        Position3 newPosition3 = particle3.getCurrentPositionClone();

        for (int i = 0; i < params.getDimensionCount(); i++) {

            BigDecimal r1 = BigDecimal.valueOf(random.nextInt());
            BigDecimal r2 = BigDecimal.valueOf(random.nextInt());

            newVelocity3.setAtDimension(i, (params.getInertiaWeight().multiply(particle3.getVelocity3(i))
                    .add(params.getC1().multiply(r1).multiply(particle3.getCoordinateOfBestPosition(i).subtract(particle3.getCoordinateOfCurrentPosition(i))))
                    .add(params.getC2().multiply(r2).multiply(globalBestPosition3.getCoordinateByIndex(i).subtract(particle3.getCoordinateOfCurrentPosition(i)))))
                    .setScale(10, RoundingMode.HALF_UP));

            newPosition3.setAtDimension(i,
                    particle3.getCoordinateOfCurrentPosition(i).add(newVelocity3.getVelocities()[i]).setScale(10, RoundingMode.HALF_UP));
        }

        particle3.setVelocity3(newVelocity3);
        particle3.setPosition3(newPosition3);
    }

    // 主算法
    public void optimize() {
        for (int iter = 0; iter < params.getMaxIterations(); iter++) {
            for (Particle3 particle3 : particle3List) {
                // 调用仿真
                particle3.updateFitnessValue(evaluateFitness(particle3.getCurrentPositionClone()));

                // 更新全局最优
                if (particle3.getFitnessValue().compareTo(this.globalBestFitness) < 0) {
                    this.globalBestFitness = particle3.getFitnessValue();
                    this.globalBestPosition3 = particle3.getCurrentPositionClone();
                }
            }

            // 更新粒子的速度和位置
            for (Particle3 particle3 : particle3List) {
                updateVelocityAndPosition(particle3);
            }

            System.out.println("Iteration " + iter + " - Best Fitness: " + globalBestFitness);
        }
    }
}
