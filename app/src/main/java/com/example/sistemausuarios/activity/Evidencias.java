package com.example.sistemausuarios.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.ServiciodbHelper;
import com.example.sistemausuarios.data.ServicioContract;
import com.example.sistemausuarios.utils.ViewAnimation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Evidencias extends AppCompatActivity {

    private final String CARPETA_RAIZ="evidencias/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    final  int COD_SELECCIONA = 10;
    final  int COD_FOTO =20;

    String path;
    ImageView imagen1;

    EditText txtViajes;
    public JSONObject jsonObject;
    String msj = "";
    private TextView textView;
    public TextView textView1;
    String info;
    String servicio;

    ServiciodbHelper serv;
    private int requestCode;
    private int resultCode;
    private Intent data;
    private Button foto;
    Button botonCargar, botonEnviar;
    private Bitmap mBitmap;
    String nombreimagen;

    private final static int LOADING_DURATION = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencias);

        textView = findViewById(R.id.info);
        textView1 = findViewById(R.id.cabecera);

        botonCargar= findViewById(R.id.btnFoto);
        botonEnviar= findViewById(R.id.btnEnviar);
        imagen1 = findViewById(R.id.imagenid);

        if(validapermisos()){
            botonCargar.setEnabled(true);
        }else{
            botonCargar.setEnabled(false);
        }

        final LinearLayout lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.GONE);

        serv = new ServiciodbHelper(this);

        consultar();



        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tomarfotografia();
            }
        });

        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingAndDisplayContent();
            }
        });

       initToolbar();
    }



    private boolean validapermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((checkSelfPermission(CAMERA)!=PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }
        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(Evidencias.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe de aceptar los permisos para el correcto funciomamiento de la App");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length ==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                botonCargar.setEnabled(true);
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                solictarpermisosmanual();
            }
        }
    }

    private void solictarpermisosmanual() {
        final CharSequence[] opciones = {"si", "no"};
        AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(Evidencias.this);
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
                textView1.setText("EVIDENCIAS DEL SERVICIO  " + Servicio);
                servicio = Servicio;
            }

        } catch (Exception e) {
            System.out.println(e);
            Toast t = Toast.makeText(Evidencias.this.getApplicationContext(), "Servicio no existe" + e, Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Carga de imagenes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Tools.setSystemBarColor(this, R.color.red_800);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            switch (item.getItemId()){
                case R.id.action_about:
                    Intent intent = new Intent(Evidencias.this, AboutApp.class);
                    startActivity(intent);
                    break;
                case R.id.action_salir:
                    Imei.cambiarEstado(Evidencias.this, false);
                    Login.cambiarEstado(Evidencias.this, false);
                    Intent intent1 = new Intent(Evidencias.this,Login.class);
                    startActivity(intent1);
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadingAndDisplayContent() {
        final LinearLayout lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimation.fadeOut(lyt_progress);
            }
        }, LOADING_DURATION);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Evidencias.Eviden cargaAsyn = new Evidencias.Eviden();
                cargaAsyn.execute();
            }
        }, 600);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main_drawer, menu);
        return true;
    }

    private void tomarfotografia(){
        File fileimagen = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean iscreada = fileimagen.exists();
        if(iscreada == false){
            iscreada = fileimagen.mkdirs();
        }
        if(iscreada == true){
            nombreimagen = (System.currentTimeMillis()/1000+".jpg");
        }
        path = Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreimagen;
        File imagen = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            String authorities = getApplicationContext().getPackageName()+".provider";
            Uri imageuri = FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        }else{
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imagen));
        }
         startActivityForResult(intent,COD_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode==RESULT_OK){
     /*   switch (requestCode){
            case COD_SELECCIONA:
                Uri mipath = data.getData();
                imagen1.setImageURI(mipath);
                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(this, new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                            Log.i("Ruta de almacenamiento","Path "+path);
                            }
                        });
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mBitmap = BitmapFactory.decodeFile(path);
                imagen1.setImageBitmap(bitmap);
                break;
        }*/
        MediaScannerConnection.scanFile(this, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        Log.i("Ruta de almacenamiento","Path "+path);
                    }
                });
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        mBitmap = BitmapFactory.decodeFile(path);
        imagen1.setImageBitmap(bitmap);
//        Uri path = data.getData();
//        imagen1.setImageURI(path);
    }

    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Evidencias.this, Menu.class);
                startActivity(intent2);
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            System.gc();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("PictureDemo", "Out of memory error catched");
        }
        return temp;
    }

    private class Eviden extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            String encodedImage = bitmapToString(mBitmap);

            Boolean resultado = null;

            HttpClient httpClient = new DefaultHttpClient();
          //  String url = "http://10.0.2.2:8080/WSerp/webresources/Evidencias/agregar";
      //     String url = "http://192.168.1.100:8282/WSerp/webresources/Evidencias/agregar";
            String url = "http://autotransportesleo.ddns.net:8282" +
                    "/WSerp/webresources/Evidencias/agregar";
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("content-type", "application/json");
            try {
                JSONObject datos =  new JSONObject();
                datos.put("noservicio",servicio);
                datos.put("nombreimg",encodedImage);
                datos.put("nombre",nombreimagen);
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
                showCustomDialog();
            } else {
                Toast t = Toast.makeText(Evidencias.this.getApplicationContext(), "La imagen no pudo ser enviada, por favor intenta nuevamente.", Toast.LENGTH_LONG);
                t.show();
            }
        }
    }


}
