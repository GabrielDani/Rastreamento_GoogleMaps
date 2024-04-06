package br.com.gabrieldani.maps.utils;

import android.location.Location;

import br.com.gabrieldani.maps.model.Region;

public class DistanceCalculator {
    private static final int MAX_DISTANCE = 30;

    // Método para calcular a distância entre dois pontos em metros
    public static float calculateDistance(Region region1, Region region2) {
        double lat1 = region1.getLatitude();
        double lon1 = region1.getLongitude();
        double lat2 = region2.getLatitude();
        double lon2 = region2.getLongitude();
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    // Método para verificar se a distância é menor que 30 metros
    public static boolean isWithinRadius(Region region1, Region region2) {
        float distance = calculateDistance(region1, region2);
        return distance < MAX_DISTANCE;
    }

    // Método para calcular a distância entre duas regiões em metros
    public static double calculateDistance2(Region region1, Region region2) {
        double lat1 = region1.getLatitude();
        double lon1 = region1.getLongitude();
        double lat2 = region2.getLatitude();
        double lon2 = region2.getLongitude();

        // Raio da Terra em metros
        final int R = 6371 * 1000; // raio da Terra em metros

        // Converte as latitudes e longitudes de graus para radianos
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Aplica a fórmula de Haversine
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calcula a distância
        return R * c;
    }
}
