package org.optimizer;

import java.util.Random;

public class Particle {
    private double[] position;
    private double[] velocity;
    private double[] bestPosition;
    private double bestValue;

    public Particle(int dimension, double min, double max) {
        Random random = new Random();
        position = new double[dimension];
        velocity = new double[dimension];
        bestPosition = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            position[i] = min + random.nextDouble() * (max - min); // 初始化位置
            velocity[i] = random.nextDouble() * (max - min) * 0.1; // 初始化速度
        }

        System.arraycopy(position, 0, bestPosition, 0, dimension);
        bestValue = Double.MAX_VALUE; // 初始适应度值设为最大
    }

    public double[] getPosition() {
        return position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public double[] getBestPosition() {
        return bestPosition;
    }

    public double getBestValue() {
        return bestValue;
    }

    public void setBestValue(double value) {
        bestValue = value;
    }

    public void updateBestPosition() {
        System.arraycopy(position, 0, bestPosition, 0, position.length);
    }

    public void updatePosition(double min, double max) {
        for (int i = 0; i < position.length; i++) {
            position[i] += velocity[i];
            // 限制粒子位置在[min, max]范围内
            if (position[i] < min) position[i] = min;
            if (position[i] > max) position[i] = max;
        }
    }

    public void updateVelocity(double[] globalBest, double c1, double c2, double w) {
        Random random = new Random();
        for (int i = 0; i < velocity.length; i++) {
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            velocity[i] = w * velocity[i]
                    + c1 * r1 * (bestPosition[i] - position[i])
                    + c2 * r2 * (globalBest[i] - position[i]);
        }
    }
}
