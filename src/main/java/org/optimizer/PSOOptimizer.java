package org.optimizer;

import java.util.Random;

public class PSOOptimizer {
    private Particle[] particles;
    private double[] globalBestPosition;
    private double globalBestValue;

    public PSOOptimizer(int numParticles, int dimension, double min, double max) {
        particles = new Particle[numParticles];
        globalBestPosition = new double[dimension];
        globalBestValue = Double.MAX_VALUE;

        for (int i = 0; i < numParticles; i++) {
            particles[i] = new Particle(dimension, min, max);
        }
    }

    public void optimize(int iterations, double c1, double c2, double w, FitnessFunction fitnessFunction) {
        for (int iter = 0; iter < iterations; iter++) {
            for (Particle particle : particles) {
                double fitnessValue = fitnessFunction.evaluate(particle.getPosition());

                // 更新个体最优
                if (fitnessValue < particle.getBestValue()) {
                    particle.setBestValue(fitnessValue);
                    particle.updateBestPosition();
                }

                // 更新全局最优
                if (fitnessValue < globalBestValue) {
                    globalBestValue = fitnessValue;
                    System.arraycopy(particle.getPosition(), 0, globalBestPosition, 0, globalBestPosition.length);
                }
            }

            // 更新粒子位置和速度
            for (Particle particle : particles) {
                particle.updateVelocity(globalBestPosition, c1, c2, w);
                particle.updatePosition(-10, 10); // 假设搜索范围[-10, 10]
            }

            System.out.println("Iteration " + iter + ": Global Best Value = " + globalBestValue);
        }
    }

    public static void main(String[] args) {
        PSOOptimizer optimizer = new PSOOptimizer(30, 5, -10, 10);
        optimizer.optimize(100, 1.5, 1.5, 0.8, position -> {
            // 示例目标函数：Sphere Function
            double sum = 0.0;
            for (double x : position) {
                sum += x * x;
            }
            return sum;
        });
    }
}

// 接口定义适应度函数
@FunctionalInterface
interface FitnessFunction {
    double evaluate(double[] position);
}
