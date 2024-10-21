package com.example.trabalhogps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
//Componente 1 – Posição do usuário

public class CompassView extends View {
    private float direction; // O rumo em graus (0-360)
    private Paint paint;

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED); // Cor do rumo
        paint.setStrokeWidth(8); // Largura da linha
        paint.setAntiAlias(true);
    }

    // Método para atualizar o rumo
    public void setDirection(float direction) {
        this.direction = direction;
        invalidate(); // Redesenha a view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float centerX = width / 2;
        float centerY = height / 2;
        float radius = Math.min(centerX, centerY) - 20; // Define o raio da bússola com uma margem

        // Configurar a pintura para o círculo da bússola
        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);
        circlePaint.setColor(Color.LTGRAY); // Cor para o círculo da bússola

        // Desenhar o círculo da bússola
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Configurar a pintura para o texto das direções
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Desenhar as direções (N, S, E, W)
        canvas.drawText("N", centerX, centerY - radius + 40, textPaint); // Norte
        canvas.drawText("S", centerX, centerY + radius - 10, textPaint); // Sul
        canvas.drawText("E", centerX + radius - 30, centerY + 10, textPaint); // Leste
        canvas.drawText("W", centerX - radius + 30, centerY + 10, textPaint); // Oeste

        // Configurar a pintura para o ponteiro
        Paint pointerPaint = new Paint();
        pointerPaint.setColor(Color.RED);
        pointerPaint.setStrokeWidth(10); // Espessura maior para o ponteiro
        pointerPaint.setStyle(Paint.Style.STROKE);
        pointerPaint.setAntiAlias(true); // Suaviza as bordas

        // Desenhar o ponteiro representando o rumo
        float pointerLength = radius - 30; // Tamanho do ponteiro
        float endX = centerX + (float) (pointerLength * Math.cos(Math.toRadians(direction)));
        float endY = centerY + (float) (pointerLength * Math.sin(Math.toRadians(direction)));

        // Desenhar o ponteiro
        canvas.drawLine(centerX, centerY, endX, endY, pointerPaint);

        // Desenhar o círculo central no ponteiro
        Paint centerCirclePaint = new Paint();
        centerCirclePaint.setStyle(Paint.Style.FILL);
        centerCirclePaint.setColor(Color.RED); // Círculo central do ponteiro
        canvas.drawCircle(centerX, centerY, 15, centerCirclePaint);
    }
}
