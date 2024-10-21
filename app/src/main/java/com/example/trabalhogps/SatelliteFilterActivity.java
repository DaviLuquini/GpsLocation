package com.example.trabalhogps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
//Componente 2 – Posições dos Satélites na Esfera Celeste
public class SatelliteFilterActivity extends AppCompatActivity {

    private CheckBox gpsCheckBox;
    private CheckBox galileoCheckBox;
    private CheckBox glonassCheckBox;
    private CheckBox usedInFixCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite_filter);

        gpsCheckBox = findViewById(R.id.checkbox_gps);
        galileoCheckBox = findViewById(R.id.checkbox_galileo);
        glonassCheckBox = findViewById(R.id.checkbox_glonass);
        usedInFixCheckBox = findViewById(R.id.checkbox_used_in_fix);

        Button applyButton = findViewById(R.id.button_apply_filters);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar uma Intent para retornar os filtros aplicados
                Intent resultIntent = new Intent();
                resultIntent.putExtra("gps", gpsCheckBox.isChecked());
                resultIntent.putExtra("galileo", galileoCheckBox.isChecked());
                resultIntent.putExtra("glonass", glonassCheckBox.isChecked());
                resultIntent.putExtra("usedInFix", usedInFixCheckBox.isChecked());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
