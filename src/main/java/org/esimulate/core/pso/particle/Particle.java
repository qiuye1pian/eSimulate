package org.esimulate.core.pso.particle;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;


public class Particle {

    @Setter
    private Position position; // 粒子的位置
    @Setter
    private Velocity velocity; // 粒子的速度
    private Position bestPosition; // 粒子的历史最优位置

    @Getter
    @Setter
    private BigDecimal fitnessValue; //当前适应度值
    @Getter
    private BigDecimal bestFitnessValue; //历史最优适应度值

    public Particle(List<Dimension> dimensionsList) {
        int dimension = dimensionsList.size();

        position = new Position(dimensionsList);

        velocity = new Velocity(new BigDecimal[dimension]);

        bestPosition = position.clone();

        // 初始化为极大值
        fitnessValue = BigDecimal.valueOf(Double.MAX_VALUE);
        bestFitnessValue = BigDecimal.valueOf(Double.MAX_VALUE);

        initialize(dimension);
    }

    public BigDecimal getCoordinateOfBestPosition(int i) {
        return this.bestPosition.getCoordinateByIndex(i);
    }

    public BigDecimal getCoordinateOfCurrentPosition(int i) {
        return this.position.getCoordinateByIndex(i);
    }

    public BigDecimal getVelocity(int i) {
        return this.velocity.getVelocities()[i];
    }

    // 初始化粒子的位置和速度
    private void initialize(int dimension) {
        Random random = new Random();
        for (int i = 0; i < dimension; i++) {
            position.setAtDimension(i, BigDecimal.valueOf(random.nextDouble() * 10 - 5).setScale(10, RoundingMode.HALF_UP));
            velocity.setAtDimension(i, BigDecimal.valueOf(random.nextDouble() * 2 - 1).setScale(10, RoundingMode.HALF_UP));
        }
    }

    public Position getCurrentPositionClone() {
        return this.position.clone();
    }

    public void updateFitnessValue(BigDecimal fitness) {

        this.fitnessValue = fitness;

        // 更新个体最优
        if (fitness.compareTo(this.getBestFitnessValue()) < 0) {
            this.bestPosition = getCurrentPositionClone();
            this.bestFitnessValue = fitness;
        }

    }
}
