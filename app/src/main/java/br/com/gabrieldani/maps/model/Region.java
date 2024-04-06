package br.com.gabrieldani.maps.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Region {
    private String name;
    private double latitude;
    private double longitude;
    private final int user = 1;
    private long timestamp; // Timestamp

    // Construtor padrão sem argumentos
    public Region() {
        // Necessário para a serialização/desserialização pelo Firebase
    }
    public Region(double latitude, double longitude) {
        // Obtém o timestamp atual
        long currentTimeStamp = System.currentTimeMillis();
        // Formata o timestamp para incluir dia, mês, hora, minuto e segundo
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        String timestampString = sdf.format(new Date(currentTimeStamp));
        // Cria o nome da região com o ID e o timestamp
        this.name = "Region_" + timestampString;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.nanoTime(); // Adiciona o timestamp
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @NonNull
    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", user=" + user +
                ", timestamp=" + timestamp +
                '}';
    }
}
