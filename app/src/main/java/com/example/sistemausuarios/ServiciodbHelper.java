package com.example.sistemausuarios;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.sistemausuarios.data.ServicioContract;

public class ServiciodbHelper  extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "servicio.db";

    public ServiciodbHelper(@Nullable Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ServicioContract.ViajeEntry.TABLE_NAME + " ("
                + ServicioContract.ViajeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ServicioContract.ViajeEntry.NO_SERVICIO + " TEXT NOT NULL,"
                + ServicioContract.ViajeEntry.DESCRIPCION + " TEXT NOT NULL,"
                + ServicioContract.ViajeEntry.CARGAR + " TEXT NOT NULL,"
                + ServicioContract.ViajeEntry.TRANSITO + " TEXT NOT NULL,"
                + ServicioContract.ViajeEntry.ARRIVO + " TEXT NOT NULL,"
                + ServicioContract.ViajeEntry.FIN + " TEXT NOT NULL,"
                + "UNIQUE (" + ServicioContract.ViajeEntry.NO_SERVICIO + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS servicio");
    onCreate(db);
    }
}
