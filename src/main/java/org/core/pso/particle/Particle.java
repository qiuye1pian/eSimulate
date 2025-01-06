package org.core.pso.particle;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Data
public class Particle {
    private Position position; // 粒子的位置
    private Velocity velocity; // 粒子的速度
    private Position bestPosition; // 粒子的历史最优位置
    private BigDecimal fitnessValue; // 当前适应度值
    private BigDecimal bestFitnessValue; // 历史最优适应度值

    public Particle(List<Dimension> dimensionsList) {
        int dimension = dimensionsList.size();
        position = new Position(new BigDecimal[dimension]);
        velocity = new Velocity(new BigDecimal[dimension]);
        bestPosition = new Position(new BigDecimal[dimension]);
        fitnessValue = BigDecimal.valueOf(Double.MAX_VALUE); // 初始化为极大值
        bestFitnessValue = BigDecimal.valueOf(Double.MAX_VALUE);
        initialize(dimension);
    }

    public BigDecimal getCoordinateOfBestPosition(int i) {
        return this.bestPosition.getCoordinates()[i];
    }

    public BigDecimal getCoordinateOfCurrentPosition(int i) {
        return this.position.getCoordinates()[i];
    }

    // 初始化粒子的位置和速度
    private void initialize(int dimension) {
        Random random = new Random();
        for (int i = 0; i < dimension; i++) {
            position.setAtDimension(i, BigDecimal.valueOf(random.nextDouble() * 10 - 5),10, RoundingMode.HALF_UP);
            velocity.setAtDimension(i, BigDecimal.valueOf(random.nextDouble() * 2 - 1),10, RoundingMode.HALF_UP);
        }
    }
}
