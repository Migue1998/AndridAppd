package com.example.sistemausuarios.data;

public class Servicio {
    private String id;
    private String noservicio;
    private String descripcion;
    private Boolean cargar;
    private Boolean transito;
    private Boolean arrivo;
    private Boolean fin;

    public Servicio(String id, String noservicio, String descripcion, Boolean cargar, Boolean transito, Boolean arrivo, Boolean fin) {
        this.id = id;
        this.noservicio = noservicio;
        this.descripcion = descripcion;
        this.cargar = cargar;
        this.transito = transito;
        this.arrivo = arrivo;
        this.fin = fin;
    }

    public String getId() {
        return id;
    }

    public String getNoservicio() {
        return noservicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Boolean getCargar() {
        return cargar;
    }

    public Boolean getTransito() {
        return transito;
    }

    public Boolean getArrivo() {
        return arrivo;
    }

    public Boolean getFin() {
        return fin;
    }

}

