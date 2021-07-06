package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Clientes;
import com.example.latinodistribuidora.Adaptador_Productos_detalleventa;
import com.example.latinodistribuidora.Adaptador_ventas;
import com.example.latinodistribuidora.CRUD.Access_Clientes;
import com.example.latinodistribuidora.CRUD.Access_Venta;
import com.example.latinodistribuidora.Modelos.Clientes;
import com.example.latinodistribuidora.Modelos.Division;
import com.example.latinodistribuidora.Modelos.Ventas;
import com.example.latinodistribuidora.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Listar_ventas extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Ventas> lista = new ArrayList<>();
    private Adaptador_ventas adaptadorVenta;
    private int ventaseleccionado = -1;
    private Object mActionMode;
    private TextView pie;
    private EditText buscar;
    private String fechaactual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_ventas);
        pie = findViewById(R.id.id_ventas_pie);
        buscar = (EditText) findViewById(R.id.txt_buscarventa);
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        fechaactual = df.format(fecha);
        llenarLista(fechaactual);
        onClick();

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filtro= String.valueOf(s.toString());
                if(filtro.length()>=0){
                    Log.i("",filtro);
                    lista.removeAll(lista);
                    llenarListaFiltrada(fechaactual,filtro);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ventaseleccionado = position;
                //mActionMode = Listar_ventas.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });
    }



    public void llenarLista(String fechaactual){
        try{
            lv = (ListView) findViewById(R.id.lv_listarventas);
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            db.openWritable();
            Cursor c = db.getv_venta(fechaactual);
            if (c.moveToFirst()){
                do {
                    lista.add( new Ventas(c.getInt(0),c.getInt(9),c.getInt(10),c.getInt(11),
                            c.getInt(12), c.getString(1), c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6), c.getString(7),
                            c.getString(8),c.getString(13),c.getString(14)));
                }while (c.moveToNext());
            }
            adaptadorVenta = new Adaptador_ventas(this,lista);
            //adaptadorVenta.notifyDataSetChanged();
            lv.setAdapter(adaptadorVenta);
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarListaFiltrada(String fechaactual, String filtro){
        try{
            lv = (ListView) findViewById(R.id.lv_listarventas);
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            Cursor c = db.getFiltrarv_venta(fechaactual,filtro);
            if (c.moveToFirst()){
                do {
                    lista.add( new Ventas(c.getInt(0),c.getInt(9),c.getInt(10),c.getInt(11),
                            c.getInt(12), c.getString(1), c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6), c.getString(7),
                            c.getString(8),c.getString(13),c.getString(14)));
                }while (c.moveToNext());
            }
            adaptadorVenta = new Adaptador_ventas(this,lista);
            //adaptadorVenta.notifyDataSetChanged();
            lv.setAdapter(adaptadorVenta);
            db.close();
        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}