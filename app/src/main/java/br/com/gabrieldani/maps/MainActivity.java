package br.com.gabrieldani.maps;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import br.com.gabrieldani.maps.repository.FirebaseHelper;
import br.com.gabrieldani.maps.services.LocationHelper;
import br.com.gabrieldani.maps.services.RegionQueueManager;
import br.com.gabrieldani.maps.utils.PermissionManager;
import br.com.gabrieldani.maps.utils.XMLHelper;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationHelper.LocationResultListener {
    private LocationHelper locationHelper;
    private TextView latitudeValueTextView, longitudeValueTextView;
    private GoogleMap googleMap;
    private RegionQueueManager regionQueueManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o SupportMapFragment para exibir o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        // Obtém o mapa assíncronamente quando estiver pronto
        mapFragment.getMapAsync(this);

        // Localiza os TextViews para exibir a latitude e a longitude
        latitudeValueTextView = findViewById(R.id.latitudeValueTextView);
        longitudeValueTextView = findViewById(R.id.longitudeValueTextView);

        // Inicializa o LocationHelper para gerenciar as atualizações de localização
        locationHelper = new LocationHelper(this);

        // Inicializa o RegionQueueManager
        regionQueueManager = new RegionQueueManager(this, new FirebaseHelper(this));

        // Verifica e solicita permissão de localização ao iniciar a atividade, se necessário
        if (!PermissionManager.isLocationPermissionGranted(this)) {
            PermissionManager.requestLocationPermission(this);
        }
    }

    // Método chamado quando o mapa estiver pronto para uso
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Configura a referência ao GoogleMap
        this.googleMap = googleMap;
        // Inicia as atualizações de localização quando a atividade é retomada
        locationHelper.startLocationUpdates(this);
    }

    // Método chamado quando uma nova localização é recebida
    @Override
    public void onLocationResult(LatLng location) {
        // Verifica se a localização não é nula
        if (location != null) {
            // Atualiza os TextViews com a latitude e longitude
            updateTextViews(location.latitude, location.longitude);

            // Adiciona um marcador no mapa com a nova localização
            if (googleMap != null) {
                XMLHelper.addMarkerAndMoveCamera(googleMap, location, "Localização Atual");
            }
        } else {
            // Atualiza os TextViews com uma mensagem de erro
            updateTextViewsError();
        }
    }

    // Método para atualizar os TextViews com a latitude e longitude
    private void updateTextViews(double latitude, double longitude) {
        String latitudeText = String.valueOf(latitude);
        String longitudeText = String.valueOf(longitude);

        latitudeValueTextView.setText(latitudeText);
        longitudeValueTextView.setText(longitudeText);
    }

    // Método para atualizar os TextViews com uma mensagem de erro
    private void updateTextViewsError() {
        XMLHelper.updateTextViewText(latitudeValueTextView, "Latitude: Error");
        XMLHelper.updateTextViewText(longitudeValueTextView, "Longitude: Error");
    }

    // Método chamado quando a atividade está sendo retomada
    @Override
    protected void onResume() {
        super.onResume();
        // Inicia as atualizações de localização quando a atividade é retomada
        if (googleMap != null) {
            locationHelper.startLocationUpdates(this);
        }
    }

    // Método chamado quando a atividade está sendo pausada
    @Override
    protected void onPause() {
        super.onPause();
        // Pára as atualizações de localização quando a atividade é pausada
        locationHelper.stopLocationUpdates();
    }

    // Método chamado quando a atividade está sendo destruída
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Pára as atualizações de localização quando a atividade é destruída
        locationHelper.stopLocationUpdates();
    }

    public void addRegion(View view) {
        // Obtem o texto dos TextViews de latitude e longitude
        String latitudeText = latitudeValueTextView.getText().toString();
        String longitudeText = longitudeValueTextView.getText().toString();

        // Verifica se os TextViews estão vazios
        if (!latitudeText.isEmpty() && !longitudeText.isEmpty()) {
            // Tenta converter os valores de latitude e longitude para double
            try {
                double latitude = Double.parseDouble(latitudeText);
                double longitude = Double.parseDouble(longitudeText);

                // Adiciona a região à fila usando RegionQueueManager
                regionQueueManager.addRegionAsync(latitude, longitude);

                // Exibe a nova região no console
                Log.d("Nova Região", "Latitude: " + latitude + ", Longitude: " + longitude);
            } catch (NumberFormatException e) {
                // Caso ocorra uma exceção ao tentar converter os valores para double
                Log.e("Erro", "Os valores de latitude e longitude não são numéricos.");
            }
        } else {
            // Caso os TextViews estejam vazios
            Log.e("Erro", "Os valores de latitude e longitude estão vazios.");
        }
    }

    public void saveToDB(View view) {
        regionQueueManager.saveToDB();
    }

}

