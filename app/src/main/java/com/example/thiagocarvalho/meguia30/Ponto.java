package com.example.thiagocarvalho.meguia30;

/**
 * Created by alexandra on 2/4/15.
 */
public class Ponto {

    private double Lat;
    private double Log;
    private String referencia;
    private boolean id;
    private int position;

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLog() {
        return Log;
    }

    public void setLog(double log) {
        Log = log;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Ponto(double lat, double log, String referencia, boolean id, int position) {
        Lat = lat;
        Log = log;
        this.referencia = referencia;
        this.id = id;
        this.position = position;
    }
}
