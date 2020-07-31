package com.example.sistemausuarios.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.ServiciodbHelper;
import com.example.sistemausuarios.TrackdbHeper;
import com.example.sistemausuarios.data.ServicioContract;
import com.example.sistemausuarios.data.TrackContract;
import com.example.sistemausuarios.utils.GPSTracker;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Menu extends AppCompatActivity {

    Button inicio, descarga, cargar, fin;
    double latitud,latitudn;
    double longitud, longitudn;
    String fecha_hora, fecha_horan;
    public String estatus, estatusnuevo;
    String msj = "";
    private TextView textView;
    private TextView textView1;
    String servicio;
    String info;
    String kilometros= "0" ;

    private GPSTracker gpsTracker;
    private Location mLocation;

    ServiciodbHelper serv;
    TrackdbHeper tracking;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;

    Boolean cargado;
    Boolean transito;
    Boolean arrivo;
    Boolean finalizo;

    Boolean conpermisos;

    String RowId;

    private View parent_view;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cargado =false;
        transito =false;
        arrivo =false;
        finalizo =false;

        setContentView(R.layout.activity_menu);

        inicio = findViewById(R.id.btn_inicar);
        cargar = findViewById(R.id.btn_carga);
        descarga = findViewById(R.id.btn_descargar);
        fin = findViewById(R.id.btn_finalizar);
        ImageButton incidentes = findViewById(R.id.incidentes);
        ImageButton evidencias = findViewById(R.id.evidencias);
        ImageButton pendientes = findViewById(R.id.pendientes);

        textView = findViewById(R.id.info);
        textView1 = findViewById(R.id.cabecera);

        layout1 = findViewById(R.id.rcargar);
        layout2 = findViewById(R.id.rtransito);
        layout3 = findViewById(R.id.rarrivo);
        layout4 = findViewById(R.id.rfin);

        parent_view = findViewById(android.R.id.content);

        conpermisos = validapermisos();

            if(conpermisos) {
                gpsTracker = new GPSTracker(getApplicationContext());
                mLocation = gpsTracker.getLocation();
                latitud = mLocation.getLatitude();
                longitud = mLocation.getLongitude();
            }

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            info = bundle.getString("infotxt");
            servicio = bundle.getString("cabecera");
            textView.setText(info);
            textView1.setText("No. de Servicio  " + servicio);
        }

        serv = new ServiciodbHelper(this);
        tracking = new TrackdbHeper(this);

        consultar();
       // borra();

        incidentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Menu.this, Incidencias.class);
                startActivity(intent2);
            }
        });

        evidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Menu.this, Evidencias.class);
                startActivity(intent2);
            }
        });

        pendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             consultarpend();
            }
        });

        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "CARGA";
                showCustomDcarga();
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "TRANSITO";
                consultarpend();
                Tracing cargaAsyn = new Tracing();
                cargaAsyn.execute();
                actualiza(2);
            }
        });

        descarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatus = "ARRIBO";
                consultarpend();
                Tracing cargaAsyn = new Tracing();
                cargaAsyn.execute();
                actualiza(3);
            }
        });

        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showCustomDfin();
            }
        });
/*
        ActivityCompat.requestPermissions(Menu.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(getApplicationContext(),"No hay Permisos",Toast.LENGTH_LONG);
            toast.show();
            return;
        }else {
        //    Toast toast = Toast.makeText(getApplicationContext(), "Ubicacion Encontrada otra vez", Toast.LENGTH_LONG);
        //    toast.show();
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
*/

        initToolbar();

    }

    private boolean validapermisos() {

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
        AlertDialog.Builder dialogo = new AlertDialog.Builder(Menu.this);
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
        AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(Menu.this);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Viaje Asignado");
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
                    Intent intent = new Intent(Menu.this, AboutApp.class);
                    startActivity(intent);
                    break;
                case R.id.action_menu:
                    Intent intent4 = new Intent(Menu.this, MainActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.action_salir:
                    Imei.cambiarEstado(Menu.this, false);
                    Login.cambiarEstado(Menu.this, false);
                    Intent intent1 = new Intent(Menu.this,Login.class);
                    startActivity(intent1);
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetTextI18n")
    private void consultar(){

        SQLiteDatabase db = serv.getReadableDatabase();
        boolean[] parametro = {false};
        String[] campos = {ServicioContract.ViajeEntry.NO_SERVICIO ,
                ServicioContract.ViajeEntry.DESCRIPCION,
                ServicioContract.ViajeEntry.CARGAR,
                ServicioContract.ViajeEntry.TRANSITO,
                ServicioContract.ViajeEntry.ARRIVO,ServicioContract.ViajeEntry.FIN};
        try {
            Cursor cursor = db.query(ServicioContract.ViajeEntry.TABLE_NAME,campos,null,null,null,null,null);
            cursor.moveToFirst();
            String Servicio = cursor.getString(0);
            String Descrip = cursor.getString(1);
            String Carga = cursor.getString(2);
            if(!cursor.equals(null)){

            if(Carga.equals("0")){
                layout1.setVisibility(View.GONE);
            }else{
                layout1.setVisibility(View.VISIBLE);
            }
            String Transito = cursor.getString(3);

            if(Transito.equals("0")){
                layout2.setVisibility(View.GONE);
            }else{
                transito =true;
                layout2.setVisibility(View.VISIBLE);
            }

            String Arrivo = cursor.getString(4);
            if(Arrivo.equals("0")){
                layout3.setVisibility(View.GONE);
            }else{
                arrivo =true;
                layout3.setVisibility(View.VISIBLE);
            }

            String Fin = cursor.getString(5);
            if(Fin.equals("0")){
                layout4.setVisibility(View.GONE);
            }else{
                finalizo =true;
                layout4.setVisibility(View.VISIBLE);
            }

            }
            db.close();
            cursor.close();

            if(servicio==null){
                textView.setText(Descrip);
                textView1.setText("No. de Servicio  " + Servicio);
                servicio = Servicio;
            }

        }catch (Exception e){
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);
            layout4.setVisibility(View.GONE);
            registraViaje();
            //Intent intent = new Intent(Menu.this, SinViajeActivity.class);
            //startActivity(intent);
            //finish();
        }
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
            Toast t = Toast.makeText(Menu.this.getApplicationContext(),"Datos registrados id " + newRowId,Toast.LENGTH_LONG);
            t.show();
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    private void actualiza(int paso){

        ServiciodbHelper dbHelper = new ServiciodbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] parametro = {servicio};
        // Contenedor de valores
        ContentValues values = new ContentValues();
        if (paso ==1){
            values.put(ServicioContract.ViajeEntry.CARGAR,true);
        }
        if (paso ==2){
            values.put(ServicioContract.ViajeEntry.TRANSITO, true);
        }
        if (paso ==3){
            values.put(ServicioContract.ViajeEntry.ARRIVO, true);
        }
        if (paso ==4){
            values.put(ServicioContract.ViajeEntry.FIN, true);
        }

        try {
            long newRowId;
            newRowId = db.update(ServicioContract.ViajeEntry.TABLE_NAME, values,ServicioContract.ViajeEntry.NO_SERVICIO+"=?", parametro);
            db.close();
            consultar();
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    private void borra(){
        ServiciodbHelper dbHelper = new ServiciodbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] parametro = {"23329"};
        // Contenedor de valores
            ContentValues values = new ContentValues();
            values.put(ServicioContract.ViajeEntry.CARGAR,false);
            values.put(ServicioContract.ViajeEntry.TRANSITO, false);
            values.put(ServicioContract.ViajeEntry.ARRIVO, false);
            values.put(ServicioContract.ViajeEntry.FIN, false);
        try {
            long newRowId;
            newRowId = db.update(ServicioContract.ViajeEntry.TABLE_NAME, values,ServicioContract.ViajeEntry.NO_SERVICIO+"=?", parametro);
            db.close();
            Toast t = Toast.makeText(Menu.this.getApplicationContext(),"Datos registrados id " + newRowId,Toast.LENGTH_LONG);
            t.show();
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    private void consultarpend(){

        Toast t1 = Toast.makeText(Menu.this.getApplicationContext(),"Buscando Pendientes por envíar " ,Toast.LENGTH_LONG);
        t1.show();
        TrackdbHeper dbHelper = new  TrackdbHeper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] campos = {TrackContract.TrackEntry.NOSERVICIO ,
                TrackContract.TrackEntry.ACCION,
                TrackContract.TrackEntry.LATITUD,
                TrackContract.TrackEntry.LONGITUD,
                TrackContract.TrackEntry.FECHA,
                TrackContract.TrackEntry._ID,
                TrackContract.TrackEntry.KMS};
        try {
            Cursor cursor = db.query(TrackContract.TrackEntry.TABLE_NAME,campos,null,null,null,null,null);

            if (cursor != null) {
                //more to the first row
                cursor.moveToFirst();
                //iterate over rows
                    //iterate over the columns
                    String Servicio = cursor.getString(0);
                    estatusnuevo = cursor.getString(1);
                    fecha_horan = cursor.getString(4);
                    latitudn = Double.parseDouble(cursor.getString(2));
                    longitudn = Double.parseDouble(cursor.getString(3));
                    kilometros = cursor.getString(6);
                    RowId = cursor.getString(5);
                            Tracingpendiente cargaAsyn1= new Tracingpendiente();
                            cargaAsyn1.execute();

                //close the cursor
                cursor.close();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void eliminatracking() {
        TrackdbHeper dbHelper = new  TrackdbHeper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TrackContract.TrackEntry._ID + " = ?";
        String[] selectionArgs = {RowId};
        try {
            long newRowId;
            newRowId = db.delete(TrackContract.TrackEntry.TABLE_NAME, selection,selectionArgs);
            db.close();
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    private void registrarAccion(String accion){
        TrackdbHeper dbHelper = new  TrackdbHeper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Contenedor de valores
        ContentValues values = new ContentValues();
        fecha_hora = obtenerFechaConFormato("yyyy-MM-dd'T'HH:mm:ss.SSSZ","America/Mexico_City");
        // Pares clave-valor
        values.put(TrackContract.TrackEntry.NOSERVICIO, servicio);
        values.put(TrackContract.TrackEntry.ACCION, accion);
        values.put(TrackContract.TrackEntry.LATITUD, latitud);
        values.put(TrackContract.TrackEntry.LONGITUD, longitud);
        values.put(TrackContract.TrackEntry.FECHA, fecha_hora);
        values.put(TrackContract.TrackEntry.ENVIADO, false);
        values.put(TrackContract.TrackEntry.KMS,kilometros);
        try {
            long newRowId;
            newRowId = db.insert(TrackContract.TrackEntry.TABLE_NAME, null, values);
            db.close();
            Toast t = Toast.makeText(Menu.this.getApplicationContext(),"Datos guardados en Telefono Tracking " + newRowId,Toast.LENGTH_LONG);
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

    @SuppressLint("SimpleDateFormat")
    public static String obtenerFechaConFormato(String formato, String zonaHoraria) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formato);
        sdf.setTimeZone(TimeZone.getTimeZone(zonaHoraria));
        return sdf.format(date);
    }

    private void showCustomDfin() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_eventfin);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_name = (EditText) dialog.findViewById(R.id.et_name);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kms = et_name.getText().toString();
                if(TextUtils.isEmpty(kms)) {
                    snackBarIconErrorkms();
                }else {
                    kilometros = kms;

                    //Falta guardar kilometraje en web services de tracking

                    estatus = "FINVIAJE";
                    consultarpend();
                    Tracing cargaAsyn = new Tracing();
                    cargaAsyn.execute();
                    actualiza(4);
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showCustomDcarga() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_event);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_name = (EditText) dialog.findViewById(R.id.et_name);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kms = et_name.getText().toString();
                if(TextUtils.isEmpty(kms)) {
                    snackBarIconErrorkms();
                }else {
                    kilometros = kms;
                    Tracing cargaAsyn = new Tracing();
                    cargaAsyn.execute();
                    actualiza(1);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void snackBarIconErrorkms() {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText("Registro no guardado, debe indicar Kilometros");
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    private void confirmar() {
        final CharSequence[] opciones = {"si", "no"};
        AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(Menu.this);
        alertaOpciones.setTitle("¿Desea realizar el registro fin de viaje?");


        alertaOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("si")) {
                    estatus = "FINVIAJE";
                    consultarpend();
                    Tracing cargaAsyn = new Tracing();
                    cargaAsyn.execute();
                    actualiza(4);
                } else {
                    Toast.makeText(getApplicationContext(), "El Cierre fue cancelado", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        alertaOpciones.show();
    }

    private void eliminaservicio() {
        ServiciodbHelper dbHelper = new ServiciodbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long newRowId;
            newRowId = db.delete(ServicioContract.ViajeEntry.TABLE_NAME, null,null);
            db.close();
            Intent intent = new Intent(Menu.this, SinViajeActivity.class);
            startActivity(intent);
            finish();
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    private void snackBarIconSuccess() {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText("Registro guardado Correctamente!");
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.green_500));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    private void snackBarIconError() {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText("Registro no guardado en Servidor");
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    private class Tracingpendiente extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean resultado = null;

            HttpClient httpClient = new DefaultHttpClient();
            //  String url = "http://10.0.2.2:8080/WSerp/webresources/Tracing/agregar";
            //   String url = "http://192.168.1.100:8282/WSerp/webresources/Tracing/agregar";
            String url = "http://autotransportesleo.ddns.net:8282/WSerp/webresources/Tracing/agregar";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("content-type", "application/json");

            try {
                JSONObject datos =  new JSONObject();

                datos.put("latitud",latitudn);
                datos.put("longitud", longitudn);
                datos.put("fecha",fecha_horan);
                datos.put("estatus", estatusnuevo);
                datos.put("ordenserviciocabecera_id",servicio);
                datos.put("idusuarioDispositivo",1);
                datos.put("kilometros",kilometros);
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
                eliminatracking();
                snackBarIconSuccess();
            } else {
                snackBarIconError();
            }
        }
    }



    private class Tracing extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean resultado = null;

            fecha_hora = obtenerFechaConFormato( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","America/Mexico_City");

                latitud = gpsTracker.getLatitude();
                longitud = gpsTracker.getLongitude();

            HttpClient httpClient = new DefaultHttpClient();
        //    String url = "http://10.0.2.2:8080/WSerp/webresources/Tracing/agregar";
         //   String url = "http://192.168.1.100:8282/WSerp/webresources/Tracing/agregar";
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
                datos.put("kilometros",kilometros);
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
                snackBarIconSuccess();
                if(estatus=="FINVIAJE"){
                    eliminaservicio();
                }

            } else {
                snackBarIconError();
                registrarAccion(estatus);
            }
        }
    }


}
