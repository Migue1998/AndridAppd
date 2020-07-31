package com.example.sistemausuarios.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.ServiciodbHelper;
import com.example.sistemausuarios.data.ServicioContract;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Viajes extends AppCompatActivity {

    EditText txtViajes;
    Button btnAceptar;
    Button btnRechazar;
    public  JSONObject jsonObject;
    String msj = "";
    private TextView textView;
    public TextView textView1;
    String info;
    String servicio;

    ServiciodbHelper serv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);


        btnAceptar = findViewById(R.id.btn_Aceptar);
        btnRechazar = findViewById(R.id.btn_Rechazar);
        textView = findViewById(R.id.info);
        textView1 = findViewById(R.id.cabecera);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            info = bundle.getString("infotxt");
            servicio = bundle.getString("cabecera");
            textView.setText(info);
            textView1.setText("No. de Servicio  " + servicio);
        }


        serv = new ServiciodbHelper(this);


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registraViaje();

                rviajes Asinc = new rviajes();
                Asinc.execute();

                Intent intent = new Intent(Viajes.this, Menu.class);
                intent.putExtra("infotxt",info);
                intent.putExtra("cabecera",servicio);
                startActivity(intent);
                finish();
            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Viajes.this, SinViajeActivity.class);
                startActivity(intent);
            }
        });



        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Confirmación de Viajes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Tools.setSystemBarColor(this, R.color.red_800);
    }

    private void registraViaje(){
        ServiciodbHelper dbHelper = new ServiciodbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Contenedor de valores
        ContentValues values = new ContentValues();
        // Pares clave-valor
        values.put(ServicioContract.ViajeEntry.NO_SERVICIO, servicio);
        values.put(ServicioContract.ViajeEntry.DESCRIPCION, info);
        values.put(ServicioContract.ViajeEntry.CARGAR,false);
        values.put(ServicioContract.ViajeEntry.TRANSITO, false);
        values.put(ServicioContract.ViajeEntry.ARRIVO, false);
        values.put(ServicioContract.ViajeEntry.FIN, false);
        try {
            long newRowId;
            newRowId = db.insert(ServicioContract.ViajeEntry.TABLE_NAME, null, values);
            db.close();
            Toast t = Toast.makeText(Viajes.this.getApplicationContext(),"Datos registrados id " + newRowId,Toast.LENGTH_LONG);
            t.show();
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            switch (item.getItemId()){
                case R.id.action_about:
                    Intent intent = new Intent(Viajes.this, AboutApp.class);
                    startActivity(intent);
                    break;
                case R.id.action_salir:
                    Imei.cambiarEstado(Viajes.this, false);
                    Login.cambiarEstado(Viajes.this, false);
                    Intent intent1 = new Intent(Viajes.this,Login.class);
                    startActivity(intent1);
                    finish();
                    break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private class rviajes extends AsyncTask<String, Integer, Boolean> {

        @SuppressLint("WrongThread")
        @Override
        protected Boolean doInBackground(String... params) {
            boolean resultado=false;
            HttpClient httpClient = new DefaultHttpClient();
        //    String url = "http://10.0.2.2:8080/WSerp/webresources/Servicios/update";
         //   String url = "http://192.168.1.100:8282/WSerp/webresources/Servicios/update";
            String url = "http://autotransportesleo.ddns.net:8282/WSerp/webresources/Servicios/update";


            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader("content-type", "application/json");

            try {
                //objeto URl
                JSONObject datos =  new JSONObject();
                datos.put("ordenserviciocabecera_id",servicio);
                StringEntity entity = new StringEntity(datos.toString());
                httpPut.setEntity(entity);


                HttpResponse response = httpClient.execute(httpPut);
                String resp = EntityUtils.toString(response.getEntity());
                System.out.println(resp.toString());

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
        protected void onPostExecute(Boolean result){
            if(!result){
                Toast t = Toast.makeText(Viajes.this.getApplicationContext(),"No se encontro viaje seleccionado",Toast.LENGTH_LONG);
                t.show();
            }else{
                Toast t = Toast.makeText(Viajes.this.getApplicationContext(),"Servicio encontrado",Toast.LENGTH_LONG);
                t.show();
            }
        }
    }


}
