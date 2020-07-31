package com.example.sistemausuarios.data;

public class Track {

    private String noservicio;
    private String accion;
    private String latitud;
    private String longitud;
    private String fecha;
    private Boolean enviado;
    private String kms;

    public Track(String noservicio, String accion, String latitud, String longitud, String fecha, Boolean enviado, String kms) {
        this.noservicio = noservicio;
        this.accion = accion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.enviado = enviado;
        this.kms = kms;
    }

    public String getNoservicio() {
        return noservicio;
    }

    public String getAccion() {
        return accion;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getFecha() {
        return fecha;
    }

    public Boolean getEnviado() {
        return enviado;
    }
    public String getKms() {
        return kms;
    }
}
