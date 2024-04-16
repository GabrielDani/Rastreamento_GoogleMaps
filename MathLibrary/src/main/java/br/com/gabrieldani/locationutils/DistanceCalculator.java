package br.com.gabrieldani.locationutils;

import android.location.Location;

public class DistanceCalculator {
    private static final int MAX_DISTANCE = 30;

    // Método para calcular a distância entre dois pontos em metros
    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    // Método para verificar se a distância é menor que 30 metros
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2) {
        float distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance < MAX_DISTANCE;
    }
}
