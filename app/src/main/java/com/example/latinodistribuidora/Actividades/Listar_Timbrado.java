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

import com.example.latinodistribuidora.CRUD.Access_Timbrado;
import com.example.latinodistribuidora.Modelos.Timbrado;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_Timbrado extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Timbrado> lista = new ArrayList<>();
    private ArrayAdapter<String> adaptador;
    private int timbradoseleccionado = -1;
    private Object mActionMode;
    private TextView pie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_timbrado);
        pie = findViewById(R.id.id_tim_pie);
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
                timbradoseleccionado = position;
                mActionMode = Listar_Timbrado.this.startActionMode(amc);
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
            ir_a_RegistrarTimbrado(null);
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
                Timbrado timbrado = lista.get(timbradoseleccionado);
                Intent in = new Intent(getApplicationContext(), Editar_Timbrado.class);
                in.putExtra("idtimbrado", timbrado.getIdtimbrado());
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

    private void AlertaEliminacion(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Desea Desactivar el timbrado seleccionado?");
        alertDialog.setTitle("Desactivar");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                eliminarTimbrado();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void eliminarTimbrado(){
        try{
            Access_Timbrado db = Access_Timbrado.getInstance(getApplicationContext());
            Timbrado timbrado = lista.get(timbradoseleccionado);
            db.openWritable();
            long resultado = db.CerrarTimbrado(timbrado.getIdtimbrado());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Timbrado desactivado satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista();
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al desactivar timbrado", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_timbrado);
            Access_Timbrado db = Access_Timbrado.getInstance(getApplicationContext());
            Cursor c = db.getTimbrado();
            if (c.moveToFirst()){
                do {
                    lista.add( new Timbrado (c.getInt(0),c.getString(1),c.getString(2),
                            c.getString(3),c.getString(4)));
                }while (c.moveToNext());
            }
            String[] arreglo = new String[lista.size()];
            for (int i = 0;i<arreglo.length;i++){
                arreglo[i] = "Timbrado: "+lista.get(i).getTimbrado()
                        +"\nVigencia inicio: "+lista.get(i).getDesde()
                        +"\nVigencia fin: "+lista.get(i).getHasta()
                        +"\nEstado: "+lista.get(i).getEstado();
            }
            adaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_item_ld,arreglo);
            lv.setAdapter(adaptador);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" timbrado listado");
            }else{
                pie.setText(cant+" timbrados listados");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void ir_a_RegistrarTimbrado(View view){
        Intent i = new Intent(getApplicationContext(), Registar_Timbrado.class);
        startActivity(i);
        finish();
    }
}