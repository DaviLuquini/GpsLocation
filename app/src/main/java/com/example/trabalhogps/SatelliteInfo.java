package com.example.trabalhogps;

import java.io.Serializable;

public class SatelliteInfo implements Serializable {
    public int svid; // ID do satélite
    public float snr; // Qualidade do sinal
    public String constellation; // Nome da constelação
    public float azimuth; // Azimute do satélite
    public float elevation; // Elevação do satélite
    public float distance; // Distância do satélite
    public boolean usedInFix; // Indica se o satélitea está sendo usado para calcular a posição

    // Construtor
    public SatelliteInfo(int svid, String constellation, boolean usedInFix, float azimuth, float elevation, float distance, float snr) {
        this.svid = svid;
        this.constellation = constellation;
        this.usedInFix = usedInFix;
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.distance = distance;
        this.snr = snr;
    }

    public int getSvid() {
        return svid;
    }

    public float getSnr() {
        return snr;
    }

    public boolean isUsedInFix() {
        return usedInFix;
    }

    public String getConstellation() {
        return constellation;
    }
}
