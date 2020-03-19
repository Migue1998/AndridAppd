package com.example.sistemausuarios;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Locale;

public class Menu extends AppCompatActivity {

    Button inicio, descarga, cargar, fin;
    public JSONObject jsonObject;
    double latitud;
    double longitud;
    String fecha_hora;
    public String estatus;
    String msj = "";
    private LocationManager locationManager;
    private Location location;

    Button cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        inicio = findViewById(R.id.btn_inicar);
        cargar = findViewById(R.id.btn_carga);
        descarga = findViewById(R.id.btn_descargar);
        fin = findViewById(R.id.btn_finalizar);


        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "En carga";
                Tracing cargaAsyn = new Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "Iniciando Viaje";
                Tracing cargaAsyn = new Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        descarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "En descarga";
                Tracing cargaAsyn = new Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "Finalizado";
                Tracing cargaAsyn = new Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(),"No hay Permisos",Toast.LENGTH_LONG);
            toast.show();
            return;
        }else {
            Toast toast = Toast.makeText(getApplicationContext(), "Ubicacion Encontrada", Toast.LENGTH_LONG);
            toast.show();
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        cerrar = findViewById(R.id.btn_cerrar);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Imei.cambiarEstado(Menu.this, false);
                Login.cambiarEstado(Menu.this, false);
                Intent intent = new Intent(Menu.this,Imei.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private class Tracing extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean resultado = null;
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            fecha_hora = "2020-03-13";



            HttpClient httpClient = new DefaultHttpClient();
            String url = "http://10.0.2.2:8080/WSerp/rest/Tracing/agregar";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("content-type", "application/json");

            try {
                JSONObject datos =  new JSONObject();

                datos.put("latitud",latitud);

                datos.put("longitud", longitud);
                datos.put("fecha",fecha_hora);
                datos.put("estatus", estatus);
                StringEntity entity = new StringEntity(datos.toString());
                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost);
                String resp = EntityUtils.toString(response.getEntity());
                if (resp != null) {
                    resultado = true;
                } else {
                    resultado = false;
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
                resultado = false;
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean r) {
            if (r) {
                Toast t = Toast.makeText(Menu.this.getApplicationContext(), "Datos añadidos", Toast.LENGTH_LONG);
                t.show();
            } else {
                Toast t = Toast.makeText(Menu.this.getApplicationContext(), "Datos no añadidos", Toast.LENGTH_LONG);
                t.show();
            }

        }
    }


}
