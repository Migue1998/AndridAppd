package com.example.sistemausuarios.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sistemausuarios.R;
import com.example.sistemausuarios.data.ListaServicios;

import java.util.ArrayList;

public class Adaptador  extends BaseAdapter {
    private Context context;
    private ArrayList<ListaServicios> listItems;

    public Adaptador(Context context, ArrayList<ListaServicios> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ListaServicios Item = (ListaServicios) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
        ImageView ImgFoto = convertView.findViewById(R.id.imgFoto);
        TextView Titulo = convertView.findViewById(R.id.titulo);
        TextView Contenido = convertView.findViewById(R.id.contenido);

        ImgFoto.setImageResource(Item.getImgFoto());
        Titulo.setText(Item.getTitulo());
        Contenido.setText(Item.getContenido());

        return convertView;
    }
}
