package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Clientes;
import com.example.latinodistribuidora.CRUD.Access_Clientes;
import com.example.latinodistribuidora.CRUD.Access_PE;
import com.example.latinodistribuidora.CRUD.Access_Timbrado;
import com.example.latinodistribuidora.CRUD.Access_Venta;
import com.example.latinodistribuidora.Modelos.Clientes;
import com.example.latinodistribuidora.Modelos.PuntoEmision;
import com.example.latinodistribuidora.Modelos.Timbrado;
import com.example.latinodistribuidora.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Listar_ClientesVenta extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Clientes> lista = new ArrayList<>();
    private Adaptador_Clientes adaptadorClientes;
    private int clienteseleccionado = -1;
    private Object mActionMode;
    private TextView pie;
    private EditText buscar;
    private String idvendedor,vendedor;
    String puntoex;
    String estab;
    String factactual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_clientes_venta);
        buscar = findViewById(R.id.id_buscarclienteV);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            idvendedor = bundle.getString("idVendedor");
            vendedor= bundle.getString("Vendedor");
        }
        llenarLista();
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
                    llenarListaFiltrada(filtro);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void onResume() {
        super.onResume();
        lista.removeAll(lista);
        llenarLista();
    }

    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                clienteseleccionado = position;
                mActionMode = Listar_ClientesVenta.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });
    }
    private ActionMode.Callback amc = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones_vender, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_vender) {
                Clientes clientes = lista.get(clienteseleccionado);
                String fechaF = obtenerFecha();
                String establecimiento = obtenerEstab();
                String puntoexpedicion = obtenerPE();
                String factA=obtenerFactura();
                Intent in = new Intent(getApplicationContext(), Registrar_venta.class);
                in.putExtra("idcliente", clientes.getIdcliente());
                in.putExtra("razonsocial", clientes.getRazon_social());
                in.putExtra("ruc", clientes.getRuc());
                in.putExtra("fecha", fechaF);
                in.putExtra("idvendedor", idvendedor);
                in.putExtra("vendedor", vendedor);
                in.putExtra("puntoexpedicion", puntoexpedicion);
                in.putExtra("establecimiento", establecimiento);
                in.putExtra("facturaactual",factA);
                startActivity(in);
                mode.finish();
                finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }


    };

    public String obtenerFecha(){
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fechaF = df.format(fecha);
        return fechaF;
    }
    public String obtenerEstab(){
        Access_PE db = new Access_PE(getApplicationContext());
        Cursor e= db.getPEActivo();
        if(e.moveToNext()){
            estab=e.getString(1);
        }else{
            estab= "0";
        }
        return estab;
    }
    public String obtenerPE(){
        Access_PE pe = new Access_PE(getApplicationContext());
        Cursor pex= pe.getPEActivo();
        if(pex.moveToNext()){
            puntoex=pex.getString(2);
        }else{
            puntoex= "0";
        }
        return puntoex;
    }
    public String obtenerFactura(){
        Access_Venta venta = new Access_Venta(getApplicationContext());
        Cursor v = venta.getVenta();
        String fac = null;
        if(v.moveToLast()){
            fac= String.valueOf(Integer.parseInt(v.getString(1))+1);
        }else{
            Access_PE pe = new Access_PE(getApplicationContext());
            Cursor pex= pe.getPEActivo();
            if(pex.moveToNext()){
                fac= String.valueOf(pex.getInt(4));
            }else{
                fac="0";
            }
        }
        switch (fac.length()){
            case 1: factactual="000000"+fac;
                break;
            case 2: factactual="00000"+fac;
                break;
            case 3: factactual="0000"+fac;
                break;
            case 4: factactual="000"+fac;
                break;
            case 5: factactual="00"+fac;
                break;
            case 6: factactual="0"+fac;
                break;
            case 7: factactual=""+fac;
                break;
        }
        return factactual;
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_clienteV);
            Access_Clientes db = Access_Clientes.getInstance(getApplicationContext());
            Cursor c = db.getClientes();
            if (c.moveToFirst()){
                do {
                    lista.add( new Clientes (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getInt(5),c.getString(6)));
                }while (c.moveToNext());
            }
            adaptadorClientes = new Adaptador_Clientes(this,lista);
            lv.setAdapter(adaptadorClientes);
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarListaFiltrada(String filtro){
        try{
            lv = (ListView) findViewById(R.id.id_lista_clienteV);
            Access_Clientes db = Access_Clientes.getInstance(getApplicationContext());
            Cursor c = db.getFiltrarClientes(filtro);
            if (c.moveToFirst()){
                do {
                    lista.add( new Clientes (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getInt(5),c.getString(6)));
                }while (c.moveToNext());
            }
            adaptadorClientes = new Adaptador_Clientes(this,lista);
            lv.setAdapter(adaptadorClientes);
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}