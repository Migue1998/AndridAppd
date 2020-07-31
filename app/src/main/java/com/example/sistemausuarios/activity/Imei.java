package com.example.sistemausuarios.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sistemausuarios.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class Imei extends AppCompatActivity {

    Button btn_getIMEI;
    TextView resIMEI;
    String imei;
    public  JSONObject jsonObject ;
    public static ArrayList<String> numeros;
    static final Integer PHONESTATS = 0x1;
    private final String TAG=Imei.class.getSimpleName();

    private static final String STRING_PREFERENCES = "com.example.sistemausuarios";
    private static final String PREFERENCE_ESTADO_IMEI_SESION = "estado.imei.sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencias);

        if (ObtenerEstado()){
            Intent intent = new Intent(Imei.this.getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    public static void cambiarEstado(Context c, boolean b){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_ESTADO_IMEI_SESION, b).apply();
    }

    public void GuardarInicio(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_ESTADO_IMEI_SESION, btn_getIMEI.isClickable()).apply();
    }

    public boolean ObtenerEstado(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_ESTADO_IMEI_SESION, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void permiso(String permission, Integer requestcode){
        if(ContextCompat.checkSelfPermission(Imei.this,permission) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Imei.this,permission)){
                ActivityCompat.requestPermissions(Imei.this,new String[]{permission},requestcode);
            }
            else{
                ActivityCompat.requestPermissions(Imei.this,new String[]{permission},requestcode);
            }
        }else{
            imei = obtenerIMEI();
            Toast.makeText(Imei.this,"Permiso concedido",Toast.LENGTH_LONG);
        }
    }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] persmissions, @NonNull int[] grantResult){
            switch (requestCode){
                case 1:{
                    if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                        imei = obtenerIMEI();
                    }
                    else {
                        Toast.makeText(Imei.this,"Acceso denegado",Toast.LENGTH_LONG);
                    }
                    return;
                }

            }
        }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String obtenerIMEI(){
        final TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getImei();
    }
    private class ImeiAsy extends AsyncTask<String, Integer, Boolean> {

        String nombre;
        @SuppressLint("WrongThread")
        @Override
        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            HttpClient httpClient = new DefaultHttpClient();
            String url = "http://10.0.2.2:8080/WSerp/webresources/imei/"+imei+"";
            HttpGet get = new HttpGet(url);
            get.setHeader("content-type","application/json");
            try{
                HttpResponse response = httpClient.execute(get);
                String resp = EntityUtils.toString(response.getEntity());

                jsonObject = new JSONObject(resp);
                String  Resimei = jsonObject.getString("imei").toString();
                if(jsonObject.equals(null)){
                    return false;
                }
                System.out.println("Llego"+ Resimei.toString());
            }
            catch (Exception ex){
                System.out.println(ex.toString());
                resultado = false;
            }
            return resultado;
        }
        @Override
        protected void onPostExecute(Boolean res){
            if(res){
                GuardarInicio();
                Toast t = Toast.makeText(getApplicationContext(),"¡Exito! IMEI Valido",Toast.LENGTH_LONG);
                t.show();
                Intent intent = new Intent(Imei.this.getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }else {
                Toast t = Toast.makeText(getApplicationContext(),"¡Error! El IMEI de este teléfono no coincide",Toast.LENGTH_LONG);
                t.show();
            }
        }

    }

}
