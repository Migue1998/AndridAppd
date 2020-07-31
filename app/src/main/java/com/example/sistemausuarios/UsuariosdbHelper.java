package com.example.sistemausuarios;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.sistemausuarios.data.UsuarioContract;


public class UsuariosdbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "usuarios.db";

    public UsuariosdbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + UsuarioContract.UsuarioEntry.TABLE_NAME + " ("
                + UsuarioContract.UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UsuarioContract.UsuarioEntry.ID + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.NAME + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.SPECIALTY + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.PHONE_NUMBER + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.BIO + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + UsuarioContract.UsuarioEntry.ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
