package com.example.sistemausuarios.utilidades;

import com.example.sistemausuarios.data.UsuarioContract;

public class utilidades {

    public static final String TABLA_USUARIOS = "usuarios.db";
    public static final String CAMPO_USUARIO = "NAME";
    public static final String CAMPO_TELEFONO = "PHONE_NUMBER";


    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + UsuarioContract.UsuarioEntry.TABLE_NAME + " (" +
            UsuarioContract.UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UsuarioContract.UsuarioEntry.ID + " TEXT NOT NULL," +
            UsuarioContract.UsuarioEntry.NAME + " TEXT," +
            UsuarioContract.UsuarioEntry.SPECIALTY + " TEXT," +
            UsuarioContract.UsuarioEntry.PHONE_NUMBER + " TEXT," +
            UsuarioContract.UsuarioEntry.BIO + " TEXT," +
            UsuarioContract.UsuarioEntry.AVATAR_URI +" TEXT," +
            "UNIQUE (" + UsuarioContract.UsuarioEntry.ID + "))";
}
