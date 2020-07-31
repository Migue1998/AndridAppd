package com.example.sistemausuarios.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.ServiciodbHelper;
import com.example.sistemausuarios.data.ServicioContract;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.location.LocationManager.GPS_PROVIDER;
import static com.example.sistemausuarios.activity.Menu.obtenerFechaConFormato;

public class Incidencias extends AppCompatActivity {

    Button btnincidente1;
    Button btnincidente2;
    Button btnincidente3;
    Button btnincidente4;
    public String estatus;
    double latitud;
    double longitud;
    String fecha_hora;
    String servicio;
    private LocationManager locationManager;
    private Location location;
    String msj = "";
    private TextView textView1;
    ServiciodbHelper serv;

    public Criteria criteria;
    public String bestProvider;

    String voice2text; //adde

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias);
        btnincidente1 = findViewById(R.id.btninc1);
        btnincidente2 = findViewById(R.id.btninc2);
        btnincidente3 = findViewById(R.id.btninc3);
        btnincidente4 = findViewById(R.id.btninc4);
        textView1 = findViewById(R.id.cabecera);

        serv = new ServiciodbHelper(this);

        consultar();


        btnincidente1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "ACCIDENTE";
                Incidencias.Tracing cargaAsyn = new Incidencias.Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        btnincidente2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "FALLA EN MOTOR";
                Incidencias.Tracing cargaAsyn = new Incidencias.Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        btnincidente3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "PROBLEMAS NEUMATICOS";
                Incidencias.Tracing cargaAsyn = new Incidencias.Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        btnincidente4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "ACCIDENTE";
                Incidencias.Tracing cargaAsyn = new Incidencias.Tracing();
                cargaAsyn.execute();
                Toast t = Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG);
                t.show();
            }
        });

        validapermisos();

        ActivityCompat.requestPermissions(Incidencias.this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(), "No hay Permisos", Toast.LENGTH_LONG);
            toast.show();
            return;
        } else {
       //     Toast toast = Toast.makeText(getApplicationContext(), "Ubicacion Encontrada", Toast.LENGTH_LONG);
      //      toast.show();
        }
        location = locationManager.getLastKnownLocation(GPS_PROVIDER);

        initToolbar();
    }

    private boolean validapermisos() {
        if ((checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        if ((checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 1);
        }
        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(Incidencias.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe de aceptar los permisos para el correcto funciomamiento de la App");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                requestPermissions(new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 1);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
       //         Toast.makeText(this, " permission granted", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this, " permisos agregar manualamente.", Toast.LENGTH_LONG).show();
            //  solictarpermisosmanual();
        }
    }


    private void consultar() {

        SQLiteDatabase db = serv.getReadableDatabase();
        //     String[] parametro = {servicio};
        String[] campos = {ServicioContract.ViajeEntry.NO_SERVICIO, ServicioContract.ViajeEntry.DESCRIPCION};
        try {
            Cursor cursor = db.query(ServicioContract.ViajeEntry.TABLE_NAME, campos, null, null, null, null, null);
            cursor.moveToFirst();
            String Servicio = cursor.getString(0);
            String Descrip = cursor.getString(1);
            cursor.close();
            if (servicio == null) {
                textView1.setText("INCIDENCIAS DEL SERVICIO  " + Servicio);
                servicio = Servicio;
            }

        } catch (Exception e) {
            System.out.println(e);
            Toast t = Toast.makeText(Incidencias.this.getApplicationContext(), "Servicio no existe" + e, Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro de incidentes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Tools.setSystemBarColor(this, R.color.red_800);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            switch (item.getItemId()) {
                case R.id.action_about:
                    Intent intent = new Intent(Incidencias.this, AboutApp.class);
                    startActivity(intent);
                    break;
                case R.id.action_salir:
                    Imei.cambiarEstado(Incidencias.this, false);
                    Login.cambiarEstado(Incidencias.this, false);
                    Intent intent1 = new Intent(Incidencias.this, Login.class);
                    startActivity(intent1);
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main_drawer, menu);
        return true;
    }


    private class Tracing extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean resultado = null;
            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
            } else {
                latitud =-103.3439107;
                longitud = 20.566166;
            }

            fecha_hora = obtenerFechaConFormato("yyyy-MM-dd'T'HH:mm:ss.SSSZ","America/Mexico_City");


            HttpClient httpClient = new DefaultHttpClient();
          //  String url = "http://10.0.2.2:8080/WSerp/webresources/Tracing/agregar";
          //  String url = "http://192.168.1.100:8282/WSerp/webresources/Tracing/agregar";
            String url = "http://autotransportesleo.ddns.net:8282/WSerp/webresources/Tracing/agregar";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("content-type", "application/json");

            try {
                JSONObject datos =  new JSONObject();

                datos.put("latitud",latitud);
                datos.put("longitud", longitud);
                datos.put("fecha",fecha_hora);
                datos.put("estatus", estatus);
                datos.put("ordenserviciocabecera_id",servicio);
                datos.put("idusuarioDispositivo",1);
                StringEntity entity = new StringEntity(datos.toString());
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                String resp = EntityUtils.toString(response.getEntity());

                System.out.println(resp);

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
                Toast t = Toast.makeText(Incidencias.this.getApplicationContext(), "Datos añadidos", Toast.LENGTH_LONG);
                t.show();
            } else {
                Toast t = Toast.makeText(Incidencias.this.getApplicationContext(), "Datos no añadidos", Toast.LENGTH_LONG);
                t.show();
            }
        }
    }

}
