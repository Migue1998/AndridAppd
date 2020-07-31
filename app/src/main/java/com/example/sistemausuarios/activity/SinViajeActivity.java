package com.example.sistemausuarios.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.ServiciodbHelper;
import com.example.sistemausuarios.UsuariosdbHelper;
import com.example.sistemausuarios.data.UsuarioContract;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


public class SinViajeActivity extends AppCompatActivity {

    ServiciodbHelper serv;
    UsuariosdbHelper conn;
    public  JSONObject jsonObject ;
    String usuario;
    Boolean vaceptado;
    String servicio ="";
    String datosv;
    private ImageButton sincronizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_viaje2);
        serv = new ServiciodbHelper(this);
        conn = new UsuariosdbHelper(this);
        sincronizar = findViewById(R.id.sincronizar);

        validapermisos();
        consultar();

        sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinViajeActivity.bviajes Asinc = new SinViajeActivity.bviajes();
                Asinc.execute();
            }
        });


        initToolbar();
    }

    private void consultar(){
        SQLiteDatabase db = conn.getReadableDatabase();
        String[] campos = {UsuarioContract.UsuarioEntry.ID,UsuarioContract.UsuarioEntry.PHONE_NUMBER};
        try {
            Cursor cursor = db.query(UsuarioContract.UsuarioEntry.TABLE_NAME,campos,null,null,null,null,null);
            cursor.moveToFirst();
            String nombre = cursor.getString(0);
            String telefono = cursor.getString(1);
            cursor.close();
            usuario = nombre;
              Toast t = Toast.makeText(SinViajeActivity.this.getApplicationContext(), "Usuario: " + nombre, Toast.LENGTH_LONG);
              t.show();


        }catch (Exception e){
            System.out.println(e);

        }
    }


    private boolean validapermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        ){
            return true;
        }

        if((checkSelfPermission(CAMERA)!=PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ||
                (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ||
                (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},100);
        }
        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(SinViajeActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe de aceptar los permisos para el correcto funciomamiento de la Aplicación");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},100);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length ==4 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos otorgados", Toast.LENGTH_LONG).show();
            } else {
                solictarpermisosmanual();
            }
        }
    }

    private void solictarpermisosmanual() {
        final CharSequence[] opciones = {"si", "no"};
        AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(SinViajeActivity.this);
        alertaOpciones.setTitle("¿Desea configurar los permisos de manera manual?");
        alertaOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Los permisos no fueron aceptados", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        alertaOpciones.show();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sin Viaje Asignado");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Tools.setSystemBarColor(this, R.color.red_800);
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
                    Intent intent = new Intent(SinViajeActivity.this, AboutApp.class);
                    startActivity(intent);
                    break;
                case R.id.action_salir:
                    Imei.cambiarEstado(SinViajeActivity.this, false);
                    Login.cambiarEstado(SinViajeActivity.this, false);
                    Intent intent1 = new Intent(SinViajeActivity.this,Login.class);
                    startActivity(intent1);
                    finish();
                    break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private class bviajes extends AsyncTask<String, Integer, Boolean> {

        @SuppressLint("WrongThread")
        @Override
        protected Boolean doInBackground(String... params) {
            boolean resultado = true;
            vaceptado = false;

            System.out.println("entro a actualizar");
            HttpClient httpClient = new DefaultHttpClient();
         //  String url = "http://10.0.2.2:8080/WSerp/webresources/Servicios/"+usuario+"";
            //   String url = "http://192.168.1.100:8282/WSerp/webresources/Servicios/update";
            String url = "http://autotransportesleo.ddns.net:8282/WSerp/webresources/Servicios/"+usuario+"";
            HttpGet get = new HttpGet(url);
            try{
                get.setHeader("content-type","application/json");
                HttpResponse response = httpClient.execute(get);
                String resp = EntityUtils.toString(response.getEntity());
                jsonObject = new JSONObject(resp);
                    datosv = jsonObject.getString("datosviaje");
                    servicio = jsonObject.getString("ordenserviciocabecera_id");
                    vaceptado = jsonObject.getBoolean("aceptado");
                System.out.println("datosv" + datosv);
            }
            catch (Exception ex){
                System.out.println(ex.toString());
                resultado = false;
                return false;
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(!result){
                Toast t = Toast.makeText(SinViajeActivity.this.getApplicationContext(),"No se encontro viaje Asignado",Toast.LENGTH_LONG);
                t.show();
            }else{
                if(vaceptado==true){
                    Intent intent = new Intent(SinViajeActivity.this,Menu.class);
                    intent.putExtra("infotxt",datosv);
                    intent.putExtra("cabecera",servicio.toString());
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SinViajeActivity.this, Viajes.class);
                    intent.putExtra("infotxt",datosv);
                    intent.putExtra("cabecera",servicio.toString());
                    startActivity(intent);
                    finish();
                }
            }
        }
    }


}
