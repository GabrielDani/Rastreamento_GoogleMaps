package br.com.gabrieldani.maps.services;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Reconciliation {
    private final List<Map<String, String>> route1Data;
    private final List<Map<String, String>> route2Data;
    private final List<Map<String, String>> route3Data;

    private static final int NUM_SENSORS = 6;

    private final double[] y1;
    private final double[] y2;
    private final double[] y3;
    private final double[] y1StdDev;
    private final double[] y2StdDev;
    private final double[] y3StdDev;
    private final double[][] v1;
    private final double[][] v2;
    private final double[][] v3;
    private static final double[][] a = {
            {0, 1, -1, 0, 0, 0},
            {0, 0, 1, -1, 0, 0},
            {0, 0, 0, 1, -1, 0},
            {0, 0, 0, 0, 1, -1}
    };
    private final double[] yhat1;
    private final double[] yhat2;
    private final double[] yhat3;

    private final double[] y123;
    private final double[] y123StdDev;
    private double[][] v123;
    private double[] yhat123;

    public Reconciliation(List<Map<String, String>> data) {
        route1Data = new ArrayList<>();
        route2Data = new ArrayList<>();
        route3Data = new ArrayList<>();

        y1 = new double[NUM_SENSORS];
        y2 = new double[NUM_SENSORS];
        y3 = new double[NUM_SENSORS];
        y1StdDev = new double[NUM_SENSORS];
        y2StdDev = new double[NUM_SENSORS];
        y3StdDev = new double[NUM_SENSORS];

        int[] route1Counts = new int[NUM_SENSORS];
        int[] route2Counts = new int[NUM_SENSORS];
        int[] route3Counts = new int[NUM_SENSORS];

        Arrays.fill(y1, 0.0);
        Arrays.fill(y2, 0.0);
        Arrays.fill(y3, 0.0);
        Arrays.fill(y1StdDev, 0.0);
        Arrays.fill(y2StdDev, 0.0);
        Arrays.fill(y3StdDev, 0.0);
        Arrays.fill(route1Counts, 0);
        Arrays.fill(route2Counts, 0);
        Arrays.fill(route3Counts, 0);

        List<List<Double>> route1SensorTimes = new ArrayList<>(NUM_SENSORS);
        List<List<Double>> route2SensorTimes = new ArrayList<>(NUM_SENSORS);
        List<List<Double>> route3SensorTimes = new ArrayList<>(NUM_SENSORS);

        for (int i = 0; i < NUM_SENSORS; i++) {
            route1SensorTimes.add(new ArrayList<>());
            route2SensorTimes.add(new ArrayList<>());
            route3SensorTimes.add(new ArrayList<>());
        }

        for (Map<String, String> record : data) {
            String route = record.get("Rota");
            String sensorName = record.get("Sensor");
            String endTimeStr = record.get("End Time");

            if (endTimeStr == null || endTimeStr.isEmpty()) continue;

            double endTime;
            try {
                endTime = Double.parseDouble(endTimeStr);
            } catch (NumberFormatException e) {
                continue; // Pular registros com formato inválido
            }

            assert sensorName != null;
            int sensorIndex = getSensorIndex(sensorName);
            if (sensorIndex == -1) continue; // Sensor inválido

            switch (Objects.requireNonNull(route)) {
                case "Rota1":
                    route1Data.add(record);
                    y1[sensorIndex] += endTime;
                    route1Counts[sensorIndex]++;
                    route1SensorTimes.get(sensorIndex).add(endTime);
                    break;
                case "Rota2":
                    route2Data.add(record);
                    y2[sensorIndex] += endTime;
                    route2Counts[sensorIndex]++;
                    route2SensorTimes.get(sensorIndex).add(endTime);
                    break;
                case "Rota3":
                    route3Data.add(record);
                    y3[sensorIndex] += endTime;
                    route3Counts[sensorIndex]++;
                    route3SensorTimes.get(sensorIndex).add(endTime);
                    break;
                default:
                    continue;
            }
        }

        // Calcula as médias
        calculateAverages(y1, route1Counts);
        calculateAverages(y2, route2Counts);
        calculateAverages(y3, route3Counts);

        // Calcula os desvios padrão
        calculateStandardDeviations(route1SensorTimes, y1, y1StdDev);
        calculateStandardDeviations(route2SensorTimes, y2, y2StdDev);
        calculateStandardDeviations(route3SensorTimes, y3, y3StdDev);

        // Convertendo os desvios padrão para matrizes de variância
        v1 = convertToVarianceMatrix(y1StdDev);
        v2 = convertToVarianceMatrix(y2StdDev);
        v3 = convertToVarianceMatrix(y3StdDev);

        yhat1 = calculeYHat(y1, v1);
        yhat2 = calculeYHat(y2, v2);
        yhat3 = calculeYHat(y3, v3);

        // Exemplo de manipulação adicional: Exibir as médias e desvios padrão
        printAverages();
        printStandardDeviations();

        // Imprimindo as matrizes de variância
        System.out.println("---------------------------------");
        System.out.println("Matriz de Variância para Rota1:");
        printMatrix(v1);

        System.out.println("\nMatriz de Variância para Rota2:");
        printMatrix(v2);

        System.out.println("\nMatriz de Variância para Rota3:");
        printMatrix(v3);

        System.out.println("---------------------------------");
        System.out.println("yhat1: " + Arrays.toString(yhat1));
        System.out.println("yhat2: " + Arrays.toString(yhat2));
        System.out.println("yhat3: " + Arrays.toString(yhat3));

        printMetrics();

        y123 = calculateYRoutes();
        v123 = calculateVariance();
        y123StdDev = calculateStdValues();
        v123 = convertToVarianceMatrix(y123StdDev);
        yhat123 = calculeYHat(y123, v123);

        System.out.println("---------------------------------");
        System.out.println("Médias dos tempos de rota (ms):");
        System.out.println("Rota123: " + Arrays.toString(y123));
        System.out.println("---------------------------------");
        System.out.println("Desvios Padrão:");
        System.out.println("Rota123: " + Arrays.toString(y123StdDev));

        // Imprimindo as matrizes de variância
        System.out.println("---------------------------------");
        System.out.println("Matriz de Variância para Rota123:");
        printMatrix(v123);

        System.out.println("---------------------------------");
        System.out.println("yhat123: " + Arrays.toString(yhat123));

        double[] bias = calculateBias(y123, yhat123);
        double[] precision = calculatePrecision(y123StdDev);
        double[] uncertainty = calculateUncertainty(bias, precision); // Assume incerteza como desvio padrão para simplificação

        System.out.println("---------------------------------");
        System.out.println("Métricas:");
        System.out.println("Rota123" + ":");
        System.out.println("  Polarização (Bias): " + Arrays.toString(bias));
        System.out.println("  Precisão: " + Arrays.toString(precision));
        System.out.println("  Incerteza: " + Arrays.toString(uncertainty));

    }

    private void calculateAverages(double[] sensorAverages, int[] sensorCounts) {
        for (int i = 0; i < sensorAverages.length; i++) {
            if (sensorCounts[i] > 0) {
                sensorAverages[i] /= sensorCounts[i];
            }
        }
    }

    private void calculateStandardDeviations(List<List<Double>> sensorTimes, double[] sensorAverages, double[] sensorStdDevs) {
        for (int i = 0; i < sensorTimes.size(); i++) {
            List<Double> times = sensorTimes.get(i);
            double mean = sensorAverages[i]; // Média já calculada

            double sumOfSquares = 0.0;
            for (Double time : times) {
                sumOfSquares += Math.pow(time - mean, 2);
            }

            if (times.size() > 1) {
                sensorStdDevs[i] = Math.sqrt(sumOfSquares / (times.size() - 1));
            } else {
                sensorStdDevs[i] = 0.0; // Desvio padrão é zero se há apenas um valor
            }
        }
    }

    public static double[][] convertToVarianceMatrix(double[] stdDevArray) {
        int size = stdDevArray.length;

        // Criação da matriz diagonal
        double[][] varianceMatrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            double stdDevValue = stdDevArray[i] / 100.0;
            double varianceValue = Math.pow(stdDevValue, 2);
            varianceMatrix[i][i] = varianceValue; // Define o valor na diagonal
        }

        return varianceMatrix;
    }

    private int getSensorIndex(String sensorName) {
        // Mapeie o nome do sensor para o índice do array
        switch (sensorName) {
            case "Sensor1": return 0;
            case "Sensor2": return 1;
            case "Sensor3": return 2;
            case "Sensor4": return 3;
            case "Sensor5": return 4;
            case "Sensor6": return 5;
            default: return -1;
        }
    }

    private void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private void printAverages() {
        System.out.println("---------------------------------");
        System.out.println("Médias dos tempos de rota (ms):");
        System.out.println("Rota1: " + Arrays.toString(y1));
        System.out.println("Rota2: " + Arrays.toString(y2));
        System.out.println("Rota3: " + Arrays.toString(y3));
    }

    private void printStandardDeviations() {
        System.out.println("---------------------------------");
        System.out.println("Desvios Padrão:");
        System.out.println("Rota1: " + Arrays.toString(y1StdDev));
        System.out.println("Rota2: " + Arrays.toString(y2StdDev));
        System.out.println("Rota3: " + Arrays.toString(y3StdDev));
    }

    private double[] calculeYHat(double[] y, double[][] v) {
        RealMatrix aMatrix = new Array2DRowRealMatrix(a);
        RealMatrix vMatrix = new Array2DRowRealMatrix(v);
        RealMatrix yMatrix = new Array2DRowRealMatrix(y);

        try {
            // Multiplicando A * V * A^T
            RealMatrix aV = aMatrix.multiply(vMatrix);
            RealMatrix aVA = aV.multiply(aMatrix.transpose());

            // Calculando a inversa de (A * V * A^T)
            DecompositionSolver solver = new LUDecomposition(aVA).getSolver();
            RealMatrix aVAInverse = solver.getInverse();

            // Calculando A^T * inv(A * V * A^T)
            RealMatrix aTAV = aMatrix.transpose().multiply(aVAInverse);

            // Calculando V * A^T * inv(A * V * A^T) * A * y
            RealMatrix yHatMatrix = vMatrix.multiply(aTAV.multiply(aMatrix)).multiply(yMatrix);

            // Subtraindo do vetor y
            yHatMatrix = yMatrix.subtract(yHatMatrix);

            return yHatMatrix.getColumn(0);
        } catch (Exception e) {
            // Tratamento de erro para casos onde a matriz não pode ser invertida ou decomposta
            System.err.println("Erro ao calcular y_hat: " + e.getMessage());
            e.printStackTrace();
            return new double[0]; // Retorna um vetor vazio em caso de erro
        }
    }

    private void printMetrics() {
        System.out.println("---------------------------------");
        System.out.println("Métricas:");

        // Calcula e exibe a polarização (bias), precisão e incerteza para cada rota
        printMetricsForRoute("Rota1", y1, yhat1, y1StdDev);
        printMetricsForRoute("Rota2", y2, yhat2, y2StdDev);
        printMetricsForRoute("Rota3", y3, yhat3, y3StdDev);
    }

    private void printMetricsForRoute(String routeName, double[] y, double[] yhat, double[] stdDev) {
        double[] bias = calculateBias(y, yhat);
        double[] precision = calculatePrecision(stdDev);
        double[] uncertainty = calculateUncertainty(bias, precision); // Assume incerteza como desvio padrão para simplificação

        System.out.println(routeName + ":");
        System.out.println("  Polarização (Bias): " + Arrays.toString(bias));
        System.out.println("  Precisão: " + Arrays.toString(precision));
        System.out.println("  Incerteza: " + Arrays.toString(uncertainty));
    }

    private double[] calculateBias(double[] y, double[] yhat) {
        double[] bias = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            bias[i] = yhat[i] - y[i];
        }
        return bias;
    }

    private double[] calculatePrecision(double[] stdDev) {
        double[] precision = new double[stdDev.length];
        for (int i = 0; i < stdDev.length; i++) {
            precision[i] = 2*stdDev[i]; // Assume precisão como o 2x desvio padrão
        }
        return precision;
    }

    private double[] calculateUncertainty(double[] bias, double[] precision) {
        double[] uncertainty = new double[bias.length];
        for (int i = 0; i < bias.length; i++) {
            uncertainty[i] = Math.sqrt(Math.pow(bias[i], 2) + Math.pow(precision[i], 2));
        }
        return uncertainty;
    }

    private double[] calculateYRoutes() {
        double[] y = new double[NUM_SENSORS];
        for(int i=0; i<NUM_SENSORS; i++) {
            y[i] = (y1[i] + y2[i] + y3[i])/3;
        }
        return y;
    }

    private double[][] calculateVariance() {
        double[][] v123 = new double[NUM_SENSORS][NUM_SENSORS];
        for (int i = 0; i < NUM_SENSORS; i++) {
            for (int j = 0; j < NUM_SENSORS; j++) {
                if (i == j) {
                    v123[i][j] = (Math.pow(y1StdDev[i], 2) + Math.pow(y2StdDev[i], 2) + Math.pow(y3StdDev[i], 2)) / 3;
                } else {
                    v123[i][j] = 0.0;
                }
            }
        }
        return v123;
    }

    private double[] calculateStdValues() {
        double[] y123StdDev = new double[NUM_SENSORS];
        for (int i = 0; i < NUM_SENSORS; i++) {
            y123StdDev[i] = Math.sqrt((Math.pow(y1StdDev[i], 2) + Math.pow(y2StdDev[i], 2) + Math.pow(y3StdDev[i], 2)) / 3);
        }
        return y123StdDev;
    }

}
