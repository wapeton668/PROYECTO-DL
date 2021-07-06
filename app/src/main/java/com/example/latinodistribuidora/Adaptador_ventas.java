package com.example.latinodistribuidora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.latinodistribuidora.Modelos.DetalleVenta;
import com.example.latinodistribuidora.Modelos.Ventas;

import java.util.ArrayList;


public class Adaptador_ventas extends BaseAdapter {
    private final Context context;
    private ArrayList<Ventas> listItems;



    public Adaptador_ventas(Context context, ArrayList<Ventas> listItems) {
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
        Ventas item = (Ventas) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_listarventas, null);

        TextView idventa = convertView.findViewById(R.id.tv_idventa);
        TextView timbado = convertView.findViewById(R.id.tv_timbrado);
        TextView factura = convertView.findViewById(R.id.tv_factura);
        TextView fecha = convertView.findViewById(R.id.tv_fecha);
        TextView vendedor = convertView.findViewById(R.id.tv_vendedor);
        TextView cliente = convertView.findViewById(R.id.tv_cliente);
        TextView total = convertView.findViewById(R.id.tv_total);

        idventa.setText(String.valueOf(item.getId()));
        timbado.setText("Timbrado: "+item.getTimbra());
        factura.setText("Factura "+item.getCondicion()+" N°: "+item.getEst()+"-"+item.getEmision()+"-"+item.getFactura());
        fecha.setText("Fecha/Hora: "+item.getFecha());
        vendedor.setText("Vendedor: "+item.getNombre());
        cliente.setText("Nombre: "+item.getCliente()+" - RUC: "+item.getRuc());
        total.setText("TOTAL: "+String.valueOf(item.getTotal()));

        return convertView;
    }



}
