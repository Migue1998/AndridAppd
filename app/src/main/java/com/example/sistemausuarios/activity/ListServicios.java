package com.example.sistemausuarios.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.adapter.Adaptador;
import com.example.sistemausuarios.data.ListaServicios;

import java.util.ArrayList;

public class ListServicios extends AppCompatActivity {
    private ListView lvItems;
    private Adaptador adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_basic);
        lvItems = findViewById(R.id.lvItems);
        adapter = new Adaptador(this, GetArrayItems());
        lvItems.setAdapter(adapter);

        initToolbar();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Listado de Servicios");
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
                    Intent intent = new Intent(ListServicios.this, AboutApp.class);
                    startActivity(intent);
                    break;
                case R.id.action_menu:
                    Intent intent4 = new Intent(ListServicios.this, MainActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.action_salir:
                    Imei.cambiarEstado(ListServicios.this, false);
                    Login.cambiarEstado(ListServicios.this, false);
                    Intent intent1 = new Intent(ListServicios.this,Login.class);
                    startActivity(intent1);
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ListaServicios> GetArrayItems(){
        ArrayList<ListaServicios> listItems = new ArrayList<>();

        listItems.add(new ListaServicios(R.drawable.carga,"Servicio No. 1","Atlacomulco - Guadalajara, La Moderna"));
        listItems.add(new ListaServicios(R.drawable.inicio,"TITULO 3","contenido3"));
        listItems.add(new ListaServicios(R.drawable.transito,"TITULO 1","contenido1"));
        listItems.add(new ListaServicios(R.drawable.huella2,"TITULO 2","contenido2"));
        listItems.add(new ListaServicios(R.drawable.huella,"TITULO 3","contenido3"));
        listItems.add(new ListaServicios(R.drawable.fin,"TITULO 4","contenido4"));
        listItems.add(new ListaServicios(R.drawable.huella,"TITULO 5","contenido5"));
        listItems.add(new ListaServicios(R.drawable.huella,"TITULO 1","contenido1"));
        listItems.add(new ListaServicios(R.drawable.huella2,"TITULO 2","contenido2"));
        listItems.add(new ListaServicios(R.drawable.huella,"TITULO 3","contenido3"));
        listItems.add(new ListaServicios(R.drawable.huella2,"TITULO 4","contenido4"));
        listItems.add(new ListaServicios(R.drawable.huella,"TITULO 5","contenido5"));

        return listItems;
    }
}
