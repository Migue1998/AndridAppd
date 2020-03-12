package com.example.sistemausuarios;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText txt_usuario, txt_password;
    private Button btn_acceso;
    public TextView resulta;
    String msj = "";
    public  JSONObject jsonObject ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txt_usuario = (EditText) findViewById(R.id.txtusuario);
        txt_password = (EditText) findViewById(R.id.txtpassword);
        resulta = (TextView) findViewById(R.id.resultado);
        btn_acceso =  findViewById(R.id.btnacceder);

        btn_acceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAsy loginAsy = new LoginAsy();
                loginAsy.execute();
                Toast t = Toast.makeText(getApplicationContext(),msj,Toast.LENGTH_LONG);
                t.show();
            }
        });
    }

    private class LoginAsy extends AsyncTask<String, Integer, Boolean>{


        String user = txt_usuario.getText().toString();
        String pwd = txt_password.getText().toString();

        String nombre;
        Integer idusuario = 0;

        @Override
        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            HttpClient httpClient = new DefaultHttpClient();
            String url = "http://10.0.2.2:8080/WebServiceLogin/webresources/Items/login/"+user+"/"+pwd+"";
            HttpGet get = new HttpGet(url);
            get.setHeader("content-type","application/json");
            try{
                HttpResponse response = httpClient.execute(get);
                String resp = EntityUtils.toString(response.getEntity());
                jsonObject = new JSONObject(resp);
                idusuario = Integer.parseInt(jsonObject.getString("id"));
                if(jsonObject.equals(null)){
                    return false;
                }

            }
            catch (Exception ex){
                System.out.println(ex.toString());
                resultado = false;
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(!result){
                Toast t = Toast.makeText(Login.this.getApplicationContext(),"Usuario y/o contraseña incorrectos",Toast.LENGTH_LONG);
                t.show();
            }else{
                Intent intent = new Intent(Login.this,Menu.class);
                intent.putExtra("idusuario",idusuario);
                startActivity(intent);
            }
        }
    }

}
