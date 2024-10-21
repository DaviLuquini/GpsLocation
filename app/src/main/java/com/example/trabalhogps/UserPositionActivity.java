package com.example.trabalhogps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;

//Componente 1 - Posição do usuário
public class UserPositionActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private TextView textViewLocation, textViewHeading;
    private CompassView compassView;
    private Button btnChangeFormat;

    private String selectedFormat = "Graus-Minutos [+/-DDD:MM.MMMMM]";
    private Location currentLocation = null; // Armazena a última localização para reutilização

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_position);

        textViewLocation = findViewById(R.id.textview_location);
        textViewHeading = findViewById(R.id.textview_heading);
        compassView = findViewById(R.id.view_compass);
        btnChangeFormat = findViewById(R.id.btn_change_format); // Inicializa o botão de mudar formato
        Button btnGoBack = findViewById(R.id.btn_go_back);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retorna para a tela GNSS View
                Intent intent = new Intent(UserPositionActivity.this, GNSSViewActivity.class);
                startActivity(intent);
                finish(); // Fecha a tela atual
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Verifica se as permissões de localização foram concedidas
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Obtém as atualizações de localização
            startLocationUpdates();
        }

        // Configura o botão para abrir o diálogo de escolha de formato
        btnChangeFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinateFormatDialog.showFormatSelectionDialog(UserPositionActivity.this, new CoordinateFormatDialog.FormatSelectionListener() {
                    @Override
                    public void onFormatSelected(String format) {
                        selectedFormat = format; // Atualiza o formato selecionado
                        if (currentLocation != null) {
                            // Atualiza a exibição da localização com o novo formato
                            updateLocationDisplay(currentLocation);
                        }
                    }
                });
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = location; // Armazena a localização atual
                updateLocationDisplay(location); // Atualiza a exibição da localização
                // Atualiza o rumo (bearing) da localização
                float bearing = location.getBearing(); // Rumo da direção
                textViewHeading.setText("Rumo: " + bearing);
                compassView.setDirection(bearing); // Atualiza o CompassView
            }
        });
    }

    private void updateLocationDisplay(Location location) {
        if (location != null) {
            String positionText = formatCoordinates(location.getLatitude(), location.getLongitude(), location.getAltitude());
            textViewLocation.setText(positionText);
        } else {
            textViewLocation.setText("Localização não disponível");
        }
    }

    private String formatCoordinates(double latitude, double longitude, double altitude) {
        switch (selectedFormat) {
            case "Graus [+/-DDD.DDDDD]":
                return String.format("Lat: %.5f, Lon: %.5f, Alt: %.2f m", latitude, longitude, altitude);
            case "Graus-Minutos [+/-DDD:MM.MMMMM]":
                return String.format("Lat: %.0f° %.5f', Lon: %.0f° %.5f', Alt: %.2f m",
                        Math.floor(latitude), (latitude % 1) * 60,
                        Math.floor(longitude), (longitude % 1) * 60, altitude);
            case "Graus-Minutos-Segundos [+/-DDD:MM:SS.SSSSS]":
                return String.format("Lat: %.0f° %.0f' %.2f\", Lon: %.0f° %.0f' %.2f\", Alt: %.2f m",
                        Math.floor(latitude), Math.floor((latitude % 1) * 60), ((latitude % 1) * 60 % 1) * 60,
                        Math.floor(longitude), Math.floor((longitude % 1) * 60), ((longitude % 1) * 60 % 1) * 60, altitude);
            default:
                return "Formato desconhecido";
        }
    }
}
