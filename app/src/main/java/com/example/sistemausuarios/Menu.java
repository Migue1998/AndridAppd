package com.example.sistemausuarios;

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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
    String msj = "";
    private LocationManager locationManager;
    private Location location;

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
                cargaAsyn cargaAsyn = new cargaAsyn();
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

    }

    private class cargaAsyn extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean resultado = null;
            latitud = location.getLatitude();
            longitud = location.getLongitude();

            HttpClient httpClient = new DefaultHttpClient();
            String url = "(http://localhost:8080/WebServiceLogin/webresources/ites/insertar/" + latitud + "/" + longitud + ")";
            HttpGet get = new HttpGet(url);
            get.setHeader("content-type", "application/json");
            try {
                HttpResponse response = httpClient.execute(get);
                if (response != null) {
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
