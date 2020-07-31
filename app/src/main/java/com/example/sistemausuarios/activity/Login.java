package com.example.sistemausuarios.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.UsuariosdbHelper;
import com.example.sistemausuarios.data.UsuarioContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Login extends AppCompatActivity {

    private static final String TAG ="TOKEN" ;
    private EditText txt_usuario, txt_password;
    private Button btn_acceso;
    public TextView resulta;
    public  JSONObject jsonObject ;
    Boolean vaceptado = false, conviaje;
    String usuario;
    String token;
    Boolean usuarioexiste;
    String Operador;
    String servicio;
    String datosv;

    UsuariosdbHelper conn;

    private static final String STRING_PREFERENCES = "com.example.sistemausuarios";
    private static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.button.sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_login);

        conn = new UsuariosdbHelper(this);

 //borrausuarios();

        if (ObtenerEstado()){
            Intent intent = new Intent(Login.this, Menu.class);
            startActivity(intent);
            finish();
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = Objects.requireNonNull(task.getResult()).getToken();

                        Log.d(TAG, token);

                    }
                });

        txt_usuario = findViewById(R.id.txtusuario);
        txt_password = findViewById(R.id.txtpassword);
        resulta = findViewById(R.id.resultado);
        btn_acceso =  findViewById(R.id.btnacceder);

        txt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == R.id.btnacceder || actionId==EditorInfo.IME_NULL){
                    LoginAsy loginAsy = new LoginAsy();
                    loginAsy.execute();
                    return true;
                }

                return false;
            }
        });



        btn_acceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAsy loginAsy = new LoginAsy();
                loginAsy.execute();

            }
        });


    }

    public static void cambiarEstado(Context c, boolean b){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_ESTADO_BUTTON_SESION, b).apply();
    }

    public void GuardarInicio(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_ESTADO_BUTTON_SESION, btn_acceso.isClickable()).apply();
    }

    public boolean ObtenerEstado(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_ESTADO_BUTTON_SESION, false);
    }

    private void registrarUsuario(){
        UsuariosdbHelper dbHelper = new UsuariosdbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Contenedor de valores
        ContentValues values = new ContentValues();

        // Pares clave-valor
        values.put(UsuarioContract.UsuarioEntry.ID, txt_usuario.getText().toString());
        values.put(UsuarioContract.UsuarioEntry.NAME, Operador);
        values.put(UsuarioContract.UsuarioEntry.SPECIALTY, "Operador");
        values.put(UsuarioContract.UsuarioEntry.PHONE_NUMBER, "S/N");
        values.put(UsuarioContract.UsuarioEntry.BIO, Operador);
        values.put(UsuarioContract.UsuarioEntry.AVATAR_URI, "operador.jpg");

        try {
            long newRowId;
            newRowId = db.insert(UsuarioContract.UsuarioEntry.TABLE_NAME, null, values);
            db.close();
            Toast t = Toast.makeText(Login.this.getApplicationContext(),"Usuario registrados id " + newRowId,Toast.LENGTH_LONG);
            t.show();
        }
        catch (Exception ex){
            System.err.println();
        }
    }

    private void consultar(){
        try {
            SQLiteDatabase db = conn.getReadableDatabase();
            String[] campos = {UsuarioContract.UsuarioEntry.ID,UsuarioContract.UsuarioEntry.PHONE_NUMBER};
            Cursor cursor = db.query(UsuarioContract.UsuarioEntry.TABLE_NAME,campos,null,null,null,null,null);
            cursor.moveToFirst();
            cursor.close();
            usuarioexiste = true;

        }catch (Exception e){
            registrarUsuario();
            Toast t = Toast.makeText(Login.this.getApplicationContext(), "Usuario no existe" , Toast.LENGTH_LONG);
            t.show();
            usuarioexiste = false;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class LoginAsy extends AsyncTask<String, Integer, Boolean>{
        String user = txt_usuario.getText().toString();
        String pwd = txt_password.getText().toString();

        boolean esoperador = false;

        @Override
        protected Boolean doInBackground(String... params) {
            vaceptado = false;

          // String url = "http://10.0.2.2:8080/WSerp/webresources/usuarios/"+user+"/"+pwd+"/"+token+"";
         //  String url = "http://192.168.1.100:8282/WSerp/webresources/usuarios/"+user+"/"+pwd+"/"+token+"";


            String result = null;

            try{
                URL url = new URL ("http://autotransportesleo.ddns.net:8282/WSerp/webresources/usuarios/"+user+"/"+pwd+"/"+token+"");
                //Abre conexión
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setDoInput(true);
                con.setReadTimeout(100000);
                con.setConnectTimeout(150000);
                con.connect();


                InputStream is=con.getInputStream();
                if (is != null) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is));
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        reader.close();
                    } finally {
                        is.close();
                    }
                    result = sb.toString();
                }

                con.disconnect();

                assert result != null;
                jsonObject = new JSONObject(result);
                String  idusuario = jsonObject.getString("username");
                int operador = jsonObject.getInt("idOperador_id");

                if(operador > 0 ){
                   esoperador = true;
                   conviaje =  jsonObject.getBoolean("conviaje");
                   if(conviaje) {
                       datosv = jsonObject.getString("datosviajes");
                       servicio = jsonObject.getString("ordenservicio_id");
                       vaceptado = jsonObject.getBoolean("aceptado");
                   }
               }
               else{
                   esoperador = false ;
               }
                usuario = idusuario;
                if(idusuario.equals("null")){
                    return false;
                }
            }
            catch (Exception ex){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(!result){
                Toast t = Toast.makeText(Login.this.getApplicationContext(),"Usuario y/o contraseña incorrectos",Toast.LENGTH_LONG);
                t.show();
            }else{
                GuardarInicio();
                if(esoperador){
                if(conviaje){
                if(vaceptado){
                    consultar();
                    Intent intent = new Intent(Login.this,Menu.class);
                    intent.putExtra("infotxt",datosv);
                    intent.putExtra("cabecera", servicio);
                    startActivity(intent);
                }
                else{
                    consultar();
                    Intent intent = new Intent(Login.this, Viajes.class);
                    intent.putExtra("infotxt",datosv);
                    intent.putExtra("cabecera", servicio);
                    startActivity(intent);
                }}else{
                    consultar();
                    Intent intent = new Intent(Login.this, SinViajeActivity.class);
                    startActivity(intent);
                }}else{
                    //cuando es cliente
                    Intent intent = new Intent(Login.this, ListServicios.class);
                    startActivity(intent);
                }
                finish();
            }
        }
    }

}
