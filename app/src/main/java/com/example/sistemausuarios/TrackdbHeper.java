package com.example.sistemausuarios;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.sistemausuarios.data.TrackContract;

public class TrackdbHeper   extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "track.db";

    public TrackdbHeper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TrackContract.TrackEntry.TABLE_NAME + " ("
                + TrackContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TrackContract.TrackEntry.ACCION + " TEXT NOT NULL,"
                + TrackContract.TrackEntry.NOSERVICIO + " TEXT NOT NULL,"
                + TrackContract.TrackEntry.LATITUD+ " TEXT NOT NULL,"
                + TrackContract.TrackEntry.LONGITUD + " TEXT NOT NULL,"
                + TrackContract.TrackEntry.FECHA + " TEXT NOT NULL,"
                + TrackContract.TrackEntry.ENVIADO + " TEXT NOT NULL,"
                + TrackContract.TrackEntry.KMS + " TEXT NOT NULL,"
                + "UNIQUE (" + TrackContract.TrackEntry._ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
