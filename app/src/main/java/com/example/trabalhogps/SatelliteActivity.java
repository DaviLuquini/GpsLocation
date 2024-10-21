package com.example.trabalhogps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.location.GnssStatus;

import java.util.ArrayList;
import java.util.List;
//Componente 2 – Posições dos Satélites na Esfera Celeste
public class SatelliteActivity extends AppCompatActivity {
    private static final int FILTER_REQUEST_CODE = 1; // Código de solicitação para o filtro
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private TextView locationTextView;
    private SatelliteView satelliteView;
    private List<SatelliteInfo> satelliteInfos = new ArrayList<>(); // Armazena informações dos satélites
    private Handler handler = new Handler();
    private boolean isFilteringActive = false; // Flag para controle de filtro
    private boolean gps = false, galileo = false, glonass = false, usedInFix = false; // Filtros

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite);

        locationTextView = findViewById(R.id.locationTextView);
        satelliteView = findViewById(R.id.satelliteView);

        // Inicializa o LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Obtém o provider de GPS
        if (locationManager != null) {
            locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        }

        // Botão para filtrar satélites
        Button filterButton = findViewById(R.id.button_filter_satellites);
        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(SatelliteActivity.this, SatelliteFilterActivity.class);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        // Solicitar atualizações de localização e status GNSS
        startLocationAndGNSSUpdates();
    }

    public void startLocationAndGNSSUpdates() {
        // Verificação de permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Solicitar atualizações de localização
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.1f, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mostraLocation(location);  // Atualiza a localização
            }
        });

        // Registrar o callback para o status dos satélites GNSS
        locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                mostraGNSS(status);  // Atualiza os satélites
            }
        });

        // Inicia o timer para atualização contínua
        startSatelliteUpdateTimer();
    }

    private void startSatelliteUpdateTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Atualiza a visualização dos satélites com base no filtro
                updateSatelliteView();

                // Repetir o timer após 1 segundo
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateSatelliteView() {
        List<SatelliteInfo> satellitesToDisplay = isFilteringActive ? filterSatellites(satelliteInfos) : satelliteInfos;

        // Verifica se a lista de satélites a ser exibida está vazia
        if (satellitesToDisplay.isEmpty() && isFilteringActive) {
            satelliteView.setSatellites(new ArrayList<>()); // Define uma lista vazia se não houver satélites filtrados
        } else {
            satelliteView.setSatellites(satellitesToDisplay); // Atualiza a visualização com os satélites a serem exibidos
        }
    }

    private List<SatelliteInfo> filterSatellites(List<SatelliteInfo> satellites) {
        List<SatelliteInfo> filteredSatellites = new ArrayList<>();

        for (SatelliteInfo satellite : satellites) {
            boolean includeSatellite = false;

            // Verifica se o satélite deve ser incluído com base nas opções selecionadas
            if (gps && satellite.getConstellation().equals("GPS")) {
                includeSatellite = true;
            }
            if (galileo && satellite.getConstellation().equals("Galileo")) {
                includeSatellite = true;
            }
            if (glonass && satellite.getConstellation().equals("GLONASS")) {
                includeSatellite = true;
            }
            if (usedInFix && satellite.isUsedInFix()) {
                includeSatellite = true;
            }

            // Adiciona o satélite à lista filtrada se atender a algum critério
            if (includeSatellite) {
                filteredSatellites.add(satellite);
            }
        }

        return filteredSatellites; // Retorna a lista filtrada
    }

    private void mostraLocation(Location location) {
        String locationText = "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude();
        locationTextView.setText(locationText);
    }

    private float getAzimuthDegrees(int satelliteIndex, GnssStatus status) {
        return status.getAzimuthDegrees(satelliteIndex);
    }

    private float getElevationDegrees(int satelliteIndex, GnssStatus status) {
        return status.getElevationDegrees(satelliteIndex);
    }

    private void mostraGNSS(GnssStatus status) {
        int satelliteCount = status.getSatelliteCount();
        Log.d("GNSS", "Satélites visíveis: " + satelliteCount);

        satelliteInfos.clear(); // Limpa a lista antes de preencher
        for (int i = 0; i < satelliteCount; i++) {
            int svid = status.getSvid(i);
            String constellation = getConstellationName(status.getConstellationType(i));
            boolean usedInFix = status.usedInFix(i);
            float azimuth = getAzimuthDegrees(i, status);
            float elevation = getElevationDegrees(i, status);
            float distance = 200; // Exemplo de distância fixa
            float snr = status.getCn0DbHz(i); // Obtém o SNR em dB-Hz

            Log.d("GNSS", "Constellation: " + constellation + "SVID: " + svid + "usedInFix: " + usedInFix + "azimuth: " + azimuth + "elevation: " + elevation + "SNR: " + snr);

            // Cria a instância de SatelliteInfo com o SNR
            SatelliteInfo satelliteInfo = new SatelliteInfo(svid, constellation, usedInFix, azimuth, elevation, distance, snr);
            satelliteInfos.add(satelliteInfo);
        }

        // Atualiza a visualização inicial com todos os satélites
        if (!isFilteringActive) {
            satelliteView.setSatellites(satelliteInfos);
        } else {
            // Atualiza a visualização com os satélites filtrados caso o filtro esteja ativo
            updateSatelliteView();
        }
    }

    private String getConstellationName(int constellationType) {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "GLONASS";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "Galileo";
            default:
                return "Desconhecido";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            gps = data.getBooleanExtra("gps", false);
            galileo = data.getBooleanExtra("galileo", false);
            glonass = data.getBooleanExtra("glonass", false);
            usedInFix = data.getBooleanExtra("usedInFix", false);

            isFilteringActive = true; // Ativa o filtro

            // Atualiza a visualização com os satélites filtrados
            updateSatelliteView();
        } else {
            isFilteringActive = false; // Desativa o filtro se não houver resultados
            updateSatelliteView(); // Atualiza a visualização para mostrar todos os satélites
        }

    }
}
