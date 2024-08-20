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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.gabrieldani.locationutils.DistanceCalculator;
import br.com.gabrieldani.maps.repository.FirebaseHelper;
import br.com.gabrieldani.maps.services.DataReconciliation;
import br.com.gabrieldani.maps.services.LocationHelper;
import br.com.gabrieldani.maps.services.Reconciliation;
import br.com.gabrieldani.maps.services.RegionQueueManager;
import br.com.gabrieldani.maps.utils.PermissionManager;
import br.com.gabrieldani.maps.utils.SensorDataSaver;
import br.com.gabrieldani.maps.utils.XMLHelper;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationHelper.LocationResultListener {
    private LocationHelper locationHelper;
    private TextView latitudeValueTextView, longitudeValueTextView;
    private GoogleMap googleMap;
    private RegionQueueManager regionQueueManager;
    private static List<LatLng> flowMeterLocations;
    private static final LatLng START_LOCATION = new LatLng(-21.22479,-43.786065);
    private static final LatLng END_LOCATION = new LatLng(-21.2205383,-43.7411528);
    private DataReconciliation dataReconciliation;
    private SensorDataSaver sensorDataSaver;

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

        // Lista dos sensores da ROTA 1
        flowMeterLocations = new ArrayList<>();
        flowMeterLocations.add(new LatLng(-21.22479,-43.786065));
        flowMeterLocations.add(new LatLng(-21.2259629,-43.7799032));
        flowMeterLocations.add(new LatLng(-21.2282689,-43.7723732));
        flowMeterLocations.add(new LatLng(-21.2262945,-43.7634505));
        flowMeterLocations.add(new LatLng(-21.222393,-43.7510799));
        flowMeterLocations.add(new LatLng(-21.2205383,-43.7411528));
//

        // Lista dos sensores da ROTA 2
//        flowMeterLocations = new ArrayList<>();
//        flowMeterLocations.add(new LatLng(-21.22479,-43.786065));
//        flowMeterLocations.add(new LatLng(-21.21863,-43.7792537));
//        flowMeterLocations.add(new LatLng(-21.217466,-43.7706376));
//        flowMeterLocations.add(new LatLng(-21.2141712,-43.7632797));
//        flowMeterLocations.add(new LatLng(-21.2127733,-43.7494716));
//        flowMeterLocations.add(new LatLng(-21.2205383,-43.7411528));


        this.sensorDataSaver = new SensorDataSaver(this, "sensor_data.csv");
        sensorDataSaver.readData();
        dataReconciliation = new DataReconciliation(this, flowMeterLocations, sensorDataSaver);

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
            dataReconciliation.updateLocation(location);

            // Adiciona um marcador no mapa com a nova localização
            if (googleMap != null) {
                XMLHelper.addMarkerAndMoveCamera(googleMap, location, START_LOCATION, END_LOCATION, flowMeterLocations);
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

    public void reconciliation(View view) {
        List<Map<String, String>> data = sensorDataSaver.readDataAsList();
        if (data != null && !data.isEmpty()) {
            Reconciliation reconciliation = new Reconciliation(data);
        } else {
            Log.d("Reconciliation", "Nenhum dado disponível para reconciliação.");
        }
    }
}