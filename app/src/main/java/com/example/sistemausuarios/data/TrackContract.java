package com.example.sistemausuarios.data;

import android.provider.BaseColumns;

public class TrackContract {

    public static abstract class TrackEntry implements BaseColumns {
        public static final String TABLE_NAME ="track";
        public static final String NOSERVICIO = "noservicio";
        public static final String ACCION = "accion";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String FECHA = "fecha";
        public static final String KMS = "kms";
        public static final String ENVIADO = "enviado";
    }
}
