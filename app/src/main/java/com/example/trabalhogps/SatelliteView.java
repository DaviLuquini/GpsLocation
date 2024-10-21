package com.example.trabalhogps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//Componente 2 – Posições dos Satélites na Esfera Celeste
public class SatelliteView extends View {
    private Paint paint;
    private Paint textPaint; // Pintura para o texto
    private List<SatelliteInfo> satellites;
    private List<Float> satellitePositionsX; // Lista para armazenar as posições X
    private List<Float> satellitePositionsY; // Lista para armazenar as posições Y
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private float sphereRotation = 0; // Ângulo de rotação da esfera
    private static final float SPHERE_ROTATION_SPEED = 0.5f; // Velocidade de rotação leve

    // Atributos de localização
    private double userLatitude;
    private double userLongitude;
    private double previousLatitude = 0;
    private double previousLongitude = 0;

    // Instância da classe Random
    private Random random = new Random();

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    public SatelliteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context); // Passa o contexto para a função init
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE); // Cor inicial da esfera

        // Inicializando o paint para texto
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(32); // Aumentar o tamanho do texto
        textPaint.setTextAlign(Paint.Align.CENTER); // Centraliza o texto
        textPaint.setFakeBoldText(true); // Texto em negrito

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_UI);

        // Configuração do FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Intervalo de 10 segundos
        locationRequest.setFastestInterval(5000); // Intervalo mais rápido de 5 segundos
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Inicia a atualização da localização
        startLocationUpdates();
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // Obter a matriz de rotação a partir do vetor de rotação
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                // Obter a orientação a partir da matriz de rotação
                SensorManager.getOrientation(rotationMatrix, orientation);
                // Redesenha a view quando a orientação mudar
                invalidate();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Não utilizado
        }
    };

    public void setSatellites(List<SatelliteInfo> satellites) {
        this.satellites = satellites;
        if (satellitePositionsX == null || satellitePositionsY == null) {
            generateSatellitePositions(); // Gera posições
        }
        invalidate(); // Redesenhar a view
    }

    // Método para gerar posições de cada satellite
    private void generateSatellitePositions() {
        satellitePositionsX = new ArrayList<>();
        satellitePositionsY = new ArrayList<>();

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f * 0.8f; // 80% do tamanho da tela

        for (SatelliteInfo satellite : satellites) {
            // Gera um ângulo entre 0 e 360 graus para o azimute
            float randomAzimuth = random.nextFloat() * 360;

            // Gera uma altura entre 0 e o raio da esfera para a elevação
            float randomElevation = random.nextFloat() * radius;

            // Calcule as coordenadas X e Y
            float x = (float) (width / 2 + (randomElevation * Math.sin(Math.toRadians(randomAzimuth))));
            float y = (float) (height / 2 - (randomElevation * Math.cos(Math.toRadians(randomAzimuth))));

            // Armazena as posições
            satellitePositionsX.add(x);
            satellitePositionsY.add(y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Rotaciona a tela de acordo com a orientação + rotação da esfera
        canvas.rotate((float) Math.toDegrees(-orientation[0]) + sphereRotation, getWidth() / 2f, getHeight() / 2f); // Combina a rotação do sensor e a rotação da esfera

        // Desenha a esfera
        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f * 0.8f; // 80% do tamanho da tela

        // Desenha a esfera
        paint.setColor(Color.BLUE);
        canvas.drawCircle(width / 2f, height / 2f, radius, paint);

        // Desenha as linhas verticais e horizontal
        paint.setColor(Color.WHITE); // Cor das linhas
        float centerX = width / 2f;
        float centerY = height / 2f;
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, paint); // Linha vertical
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, paint); // Linha horizontal

        // Desenha cada satélite
        if (satellites != null && satellitePositionsX != null && satellitePositionsY != null) {
            for (int i = 0; i < satellites.size(); i++) {
                drawSatellite(canvas, satellites.get(i), satellitePositionsX.get(i), satellitePositionsY.get(i));
            }
        }

        // Desenha aviso sobre cores
        drawColorLegend(canvas, width, height);

        // Invalida o view para redimensionar e chamar o onDraw novamente
        invalidate(); // Redesenha a view continuamente
    }

    private void drawSatellite(Canvas canvas, SatelliteInfo satellite, float x, float y) {
        // Define a cor da bolinha com base na propriedade usedInFix
        if (satellite.usedInFix) {
            paint.setColor(Color.GREEN); // Usado no fix, cor verde
        } else {
            paint.setColor(Color.RED); // Não usado no fix, cor vermelha
        }

        // Desenha a bolinha do satélite
        canvas.drawCircle(x, y, 10, paint); // raio da bolinha

        // Desenha as informações abaixo da bolinha
        String satelliteInfo = "SVID: " + satellite.svid + "\n" + " Constelação: " + satellite.constellation;
        float textY = y + 20; // Posição do texto abaixo da bolinha

        // Certifique-se de que o texto está dentro dos limites visíveis
        if (textY < getHeight() && textY > 0) {
            canvas.drawText(satelliteInfo, x, textY, textPaint); // Desenha o texto
        }
    }

    private void drawColorLegend(Canvas canvas, int width, int height) {
        // Desenha um retângulo verde e vermelho com texto abaixo da esfera
        int legendHeight = 60;
        float centerX = width / 2f;

        // Aumentar a distância para ficar muito abaixo da esfera
        float legendStartY = height / 2f + 10 + 500; // A posição Y é ajustada para ficar bem abaixo do círculo azul

        // Desenha retângulo verde
        paint.setColor(Color.GREEN);
        canvas.drawRect(centerX - 160, legendStartY, centerX - 100, legendStartY + legendHeight, paint); // Aumenta a distância do verde

        // Desenha retângulo vermelho
        paint.setColor(Color.RED);
        canvas.drawRect(centerX + 140, legendStartY, centerX + 200, legendStartY + legendHeight, paint); // Aumenta a distância para o vermelho

        // Desenha texto de legenda
        textPaint.setColor(Color.BLACK); // Texto preto
        canvas.drawText("Usado no Fix", centerX - 130, legendStartY + legendHeight + 40, textPaint); // Ajusta a posição do texto
        canvas.drawText("Não Usado no Fix", centerX + 150, legendStartY + legendHeight + 40, textPaint); // Ajusta a posição do texto
    }

    // Método para atualizar a localização do usuário
    public void updateUserLocation(double latitude, double longitude) {
        // Verifica se é a primeira atualização de localização
        if (previousLatitude != 0 || previousLongitude != 0) {
            // Calcular a direção da rotação baseada na mudança de localização
            float azimuth = (float) calculateAzimuth(previousLatitude, previousLongitude, latitude, longitude);
            sphereRotation += azimuth * SPHERE_ROTATION_SPEED/2; // Atualiza a rotação da esfera com um ajuste leve
        }

        // Atualiza a localização anterior
        previousLatitude = latitude;
        previousLongitude = longitude;

        invalidate(); // Redesenha a view
    }

    // Método para calcular o azimute entre duas localizações
    private double calculateAzimuth(double lat1, double lon1, double lat2, double lon2) {
        double deltaLon = Math.toRadians(lon2 - lon1);
        double y = Math.sin(deltaLon) * Math.cos(Math.toRadians(lat2));
        double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(deltaLon);
        return Math.toDegrees(Math.atan2(y, x));
    }

    // Inicia as atualizações de localização
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Se a permissão não for concedida, retorna
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    // Callback para receber atualizações de localização
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                // Atualiza a localização do usuário
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                updateUserLocation(userLatitude, userLongitude); // Atualiza a visualização da esfera
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(sensorEventListener);
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
