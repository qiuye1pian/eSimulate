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
    private List<Particle> particleList = new ArrayList<>();
    private Position globalBestPosition;
    private BigDecimal globalBestFitness;


    public PSO(PSOParameters params) {
        this.params = params;
        this.globalBestPosition = new Position(params.getDimensionList());
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
            particleList.add(new Particle(params.getDimensionList()));
        }
    }

    // 适应度函数（目标函数示例：Sphere Function）
    // TODO:这里要改成调用simulator
    private BigDecimal evaluateFitness(Position position) {
        BigDecimal fitness = BigDecimal.ZERO;
        for (BigDecimal value : position.getCoordinateValueList()) {
            fitness = fitness.add(value.multiply(value));
        }
        return fitness.setScale(10, RoundingMode.HALF_UP);
    }

    // 更新速度和位置
    public void updateVelocityAndPosition(Particle particle) {

        Random random = new Random();
        Velocity newVelocity = new Velocity(new BigDecimal[params.getDimensionCount()]);
        Position newPosition = particle.getCurrentPositionClone();

        for (int i = 0; i < params.getDimensionCount(); i++) {

            BigDecimal r1 = BigDecimal.valueOf(random.nextInt());
            BigDecimal r2 = BigDecimal.valueOf(random.nextInt());

            newVelocity.setAtDimension(i, (params.getInertiaWeight().multiply(particle.getVelocity(i))
                    .add(params.getC1().multiply(r1).multiply(particle.getCoordinateOfBestPosition(i).subtract(particle.getCoordinateOfCurrentPosition(i))))
                    .add(params.getC2().multiply(r2).multiply(globalBestPosition.getCoordinateByIndex(i).subtract(particle.getCoordinateOfCurrentPosition(i)))))
                    .setScale(10, RoundingMode.HALF_UP));

            newPosition.setAtDimension(i,
                    particle.getCoordinateOfCurrentPosition(i).add(newVelocity.getVelocities()[i]).setScale(10, RoundingMode.HALF_UP));
        }

        particle.setVelocity(newVelocity);
        particle.setPosition(newPosition);
    }

    // 主算法
    public void optimize() {
        for (int iter = 0; iter < params.getMaxIterations(); iter++) {
            for (Particle particle : particleList) {
                // 调用仿真
                particle.updateFitnessValue(evaluateFitness(particle.getCurrentPositionClone()));

                // 更新全局最优
                if (particle.getFitnessValue().compareTo(this.globalBestFitness) < 0) {
                    this.globalBestFitness = particle.getFitnessValue();
                    this.globalBestPosition = particle.getCurrentPositionClone();
                }
            }

            // 更新粒子的速度和位置
            for (Particle particle : particleList) {
                updateVelocityAndPosition(particle);
            }

            System.out.println("Iteration " + iter + " - Best Fitness: " + globalBestFitness);
        }
    }
}
