package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.CRUD.Access_PE;
import com.example.latinodistribuidora.CRUD.Access_Timbrado;
import com.example.latinodistribuidora.Modelos.PuntoEmision;
import com.example.latinodistribuidora.Modelos.Timbrado;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_PE extends AppCompatActivity {
    private ListView lv;
    private ArrayList<PuntoEmision> lista = new ArrayList<>();
    private ArrayAdapter<String> adaptador;
    private int peseleccionado = -1;
    private Object mActionMode;
    private TextView pie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_pe);
        pie = findViewById(R.id.id_pe_pie);
        llenarLista();
        onClick();
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
                peseleccionado = position;
                mActionMode = Listar_PE.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opcion_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id=menuItem.getItemId();
        if(id==R.id.item_nuevo){
            ir_a_RegistrarPE(null);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private ActionMode.Callback amc = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones_upd, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_modificar) {
                PuntoEmision pe = lista.get(peseleccionado);
                Intent in = new Intent(getApplicationContext(), Editar_PE.class);
                in.putExtra("idemision", pe.getIdemision());
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

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_pe);
            Access_PE db = Access_PE.getInstance(getApplicationContext());
            Cursor c = db.getPE();
            if (c.moveToFirst()){
                do {
                    lista.add( new PuntoEmision (c.getInt(0),c.getString(1),c.getString(2),
                            c.getString(3),c.getString(4),c.getString(5),c.getString(6)));
                }while (c.moveToNext());
            }
            String[] arreglo = new String[lista.size()];
            for (int i = 0;i<arreglo.length;i++){
                arreglo[i] = "Establecimiento: "+lista.get(i).getEstablecimiento()
                        +"\nPunto de emisión: "+lista.get(i).getPe()
                        +"\nDirección: "+lista.get(i).getDireccion()
                        +"\nFacturar desde: "+lista.get(i).getDesde()
                        +"\nFacturar hasta: "+lista.get(i).getHasta()
                        +"\nEstado: "+lista.get(i).getEstado();
            }
            adaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_item_ld,arreglo);
            lv.setAdapter(adaptador);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" punto de emisión listado");
            }else{
                pie.setText(cant+" puntos de emisiones listados");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void ir_a_RegistrarPE(View view){
        Intent i = new Intent(getApplicationContext(), Registrar_PE.class);
        startActivity(i);
        finish();
    }
}