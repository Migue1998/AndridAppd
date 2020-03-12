package com.example.sistemausuarios;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Viajes extends AppCompatActivity {

    EditText txtViajes;
    Button btnAceptar;
    Button btnRechazar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);

        txtViajes = findViewById(R.id.txt_viaje);
        btnAceptar = findViewById(R.id.btn_Aceptar);
        btnRechazar = findViewById(R.id.btn_Rechazar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }



}
