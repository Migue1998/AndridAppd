package com.example.sistemausuarios.data;

import android.provider.BaseColumns;

public class ServicioContract {

    public static abstract class ViajeEntry implements BaseColumns {
        public static final String TABLE_NAME ="servicio";
        public static final String NO_SERVICIO = "noservicio";
        public static final String DESCRIPCION = "descripcion";
        public static final String CARGAR = "cargar";
        public static final String TRANSITO = "transito";
        public static final String ARRIVO = "arrivo";
        public static final String FIN = "fin";
    }
}
