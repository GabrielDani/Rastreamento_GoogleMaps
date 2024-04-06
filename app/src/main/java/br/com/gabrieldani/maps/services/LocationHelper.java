package br.com.gabrieldani.maps.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {
    // FusedLocationProviderClient é responsável por obter a localização atual do dispositivo
    private final FusedLocationProviderClient mFusedLocationProviderClient;
    // LocationRequest define os critérios para as atualizações de localização
    private final LocationRequest mLocationRequest;
    // LocationCallback recebe as atualizações de localização do FusedLocationProviderClient
    private final LocationCallback mLocationCallback;
    // Interface para notificar sobre as atualizações de localização
    private LocationResultListener mListener;

    // Interface para notificar sobre as atualizações de localização
    public interface LocationResultListener {
        void onLocationResult(LatLng location);
    }

    // Construtor da classe LocationHelper
    public LocationHelper(Context context) {
        // Obtém uma instância do FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        // Define os intervalos de tempo para as atualizações de localização
        int LOCATION_INTERVAL = 2000;
        int LOCATION_FASTEST_INTERVAL = 1000;
        // Cria um objeto LocationRequest com os critérios de atualização de localização
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
                .build();
        // Cria um objeto LocationCallback para receber as atualizações de localização
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // Itera sobre as localizações recebidas
                for (Location location : locationResult.getLocations()) {
                    // Verifica se a localização não é nula
                    if (location != null) {
                        // Converte a localização para um objeto LatLng
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // Notifica o ouvinte (se existir) sobre a nova localização
                        if (mListener != null) {
                            mListener.onLocationResult(latLng);
                        }
                    }
                }
            }
        };
    }

    // Método para iniciar as atualizações de localização
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(LocationResultListener listener) {
        // Define o ouvinte para receber notificações sobre as atualizações de localização
        mListener = listener;
        // Solicita as atualizações de localização ao FusedLocationProviderClient
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    // Método para parar as atualizações de localização
    public void stopLocationUpdates() {
        // Remove o ouvinte das atualizações de localização do FusedLocationProviderClient
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }
}
