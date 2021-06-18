package com.example.latinodistribuidora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.latinodistribuidora.Modelos.Clientes;
import com.example.latinodistribuidora.Modelos.Productos;

import java.util.ArrayList;

public class Adaptador_Clientes extends BaseAdapter {
    private Context context;
    private ArrayList<Clientes> listItems;

    public Adaptador_Clientes(Context context, ArrayList<Clientes> listItems) {
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
        Clientes item = (Clientes) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_clientes,null);
        TextView razonsocial = (TextView) convertView.findViewById(R.id.id_razonsocial);
        TextView ruc = (TextView) convertView.findViewById(R.id.id_ruc);
        TextView ciudad = (TextView) convertView.findViewById(R.id.id_ciudad);


        razonsocial.setText(item.getRazon_social());
        ruc.setText(item.getRuc());
        ciudad.setText(item.getCiudad());
        return convertView;
    }
}
