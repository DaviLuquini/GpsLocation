package com.example.trabalhogps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GNSSViewActivity extends AppCompatActivity {

    private Button btnPositionUser;
    private Button btnSatellitePositions;
    private Button btnSignalQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnss_view);

        btnPositionUser = findViewById(R.id.btn_position_user);
        btnSatellitePositions = findViewById(R.id.btn_satellite_positions);
        btnSignalQuality = findViewById(R.id.btn_signal_quality);

        // Navegando para o Componente 1 - tela de Posição do Usuário
        btnPositionUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GNSSViewActivity.this, UserPositionActivity.class);
                startActivity(intent);
            }
        });

        // Navegando para o Componente 2 -  tela de Posições dos Satélites
        btnSatellitePositions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GNSSViewActivity.this, SatelliteActivity.class);
                startActivity(intent);
            }
        });

        // Navegando para o Componente 3 - tela de Qualidade do Sinal dos Satélites
        btnSignalQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GNSSViewActivity.this, SignalQualityActivity.class);
                startActivity(intent);
            }
        });
    }
}
