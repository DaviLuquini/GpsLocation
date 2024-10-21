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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.GnssStatus;

import java.util.ArrayList;
import java.util.List;
//Componente 3 – Gráfico com a Qualidade do Sinal dos Satélites
public class SignalQualityActivity extends AppCompatActivity {
    private static final int FILTER_REQUEST_CODE = 1; // Código de solicitação para o filtro
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private TextView locationTextView;
    private SatelliteView satelliteView;
    private List<SatelliteInfo> satelliteInfos = new ArrayList<>(); // Armazena informações dos satélites
    private Handler handler = new Handler();
    private SatelliteAdapter satelliteAdapter; // Adaptador para o RecyclerView
    private boolean isFilteringActive = false; // Flag para controle de filtro
    private boolean gps = false, galileo = false, glonass = false, usedInFix = false; // Filtros

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_quality);

        locationTextView = findViewById(R.id.locationTextView);
        satelliteView = findViewById(R.id.satelliteView);

        // Inicializa o RecyclerView e o Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        satelliteAdapter = new SatelliteAdapter(this, satelliteInfos);
        recyclerView.setAdapter(satelliteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa o LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Obtém o provider de GPS
        if (locationManager != null) {
            locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        }

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
                // Não estamos utilizando a localização neste exemplo, mas poderia ser atualizada aqui
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

    }


    private void updateSatelliteView() {
        List<SatelliteInfo> satellitesToDisplay = isFilteringActive ? filterSatellites(satelliteInfos) : satelliteInfos;

        // Verifica se a lista de satélites a ser exibida está vazia
        if (satellitesToDisplay.isEmpty() && isFilteringActive) {
            satelliteInfos.clear(); // Limpa a lista se não houver satélites filtrados
        } else {
            satelliteInfos.clear();
            satelliteInfos.addAll(satellitesToDisplay); // Atualiza a lista com os satélites a serem exibidos
        }

        // Atualiza a exibição dos satélites
        satelliteAdapter.notifyDataSetChanged();
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

    private boolean satellitesInitialized = false; // Flag para verificar se os satélites já foram inicializados

    private void mostraGNSS(GnssStatus status) {
        // Verifica se os satélites já foram inicializados
        if (!satellitesInitialized) {
            int satelliteCount = status.getSatelliteCount();
            Log.d("GNSS", "Satélites visíveis: " + satelliteCount);

            for (int i = 0; i < satelliteCount; i++) {
                int svid = status.getSvid(i);
                String constellation = getConstellationName(status.getConstellationType(i));
                boolean usedInFix = status.usedInFix(i);
                float snr = status.getCn0DbHz(i); // Obtém o SNR em dB-Hz

                // Cria a instância de SatelliteInfo com o SNR
                SatelliteInfo satelliteInfo = new SatelliteInfo(svid, null, false, 0, 0, 0, snr);
                satelliteInfos.add(satelliteInfo);
            }

            // Atualiza a interface chamando notifyDataSetChanged
            satelliteAdapter.notifyDataSetChanged();

            // Marca os satélites como inicializados
            satellitesInitialized = true;
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


