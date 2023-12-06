package sensordataprocessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SensorDataProcessor {
    // Sensor data and limits.
    public double[][][] data;
    public double[][] limit;

    // Constants for readability
    private static final double MIN_AVERAGE_THRESHOLD = 10;
    private static final double MAX_AVERAGE_THRESHOLD = 50;
    private static final double CUBE_EXPONENT = 3.0;

    // constructor
    public SensorDataProcessor(double[][][] data, double[][] limit) {
        this.data = data;
        this.limit = limit;
    }

    // calculates average of sensor data
    private double average(double[] array) {
        double sum = 0;
        for (double value : array) {
            sum += value;
        }
        return sum / array.length;
    }

    // calculate data
    public void calculate(double divisor) {
        int i, j, k = 0;
        double[][][] data2 = new double[data.length][data[0].length][data[0][0].length];

        // Write racing stats data into a file
        try (BufferedWriter out = new BufferedWriter(new FileWriter("RacingStatsData.txt"))) {
            for (i = 0; i < data.length; i++) {
                for (j = 0; j < data[0].length; j++) {
                    for (k = 0; k < data[0][0].length; k++) {
                        data2[i][j][k] = data[i][j][k] / divisor - Math.pow(limit[i][j], CUBE_EXPONENT);
                        if (average(data2[i][j]) > MIN_AVERAGE_THRESHOLD && average(data2[i][j]) < MAX_AVERAGE_THRESHOLD)
                            break;
                        else if (Math.max(data[i][j][k], data2[i][j][k]) > data[i][j][k])
                            break;
                        else if (Math.pow(Math.abs(data[i][j][k]), CUBE_EXPONENT) <
                                Math.pow(Math.abs(data2[i][j][k]), CUBE_EXPONENT)
                                && average(data[i][j]) < data2[i][j][k] && (i + 1) * (j + 1) > 0)
                            data2[i][j][k] *= 2;
                        else
                            continue;
                    }
                }
            }
            writeDataToFile(out, data2);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeDataToFile(BufferedWriter out, double[][][] processedData) throws IOException {
        for (double[][] matrix : processedData) {
            for (double[] row : matrix) {
                for (double value : row) {
                    out.write(value + "\t");
                }
            }
        }
    }
}
