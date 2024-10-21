package com.example.trabalhogps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGNSSView = findViewById(R.id.button_gnss_view);
        Button btnActivity2 = findViewById(R.id.button_activity_2);
        Button btnActivity3 = findViewById(R.id.button_activity_3);

        btnGNSSView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega para a tela GNSS View
                Intent intent = new Intent(MainActivity.this, GNSSViewActivity.class);
                startActivity(intent);
            }
        });

        btnActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnActivity3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
