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

import com.example.latinodistribuidora.CRUD.Access_Division;
import com.example.latinodistribuidora.Modelos.Division;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_Division extends AppCompatActivity {

    private ListView lv;
    private ArrayList<Division> lista = new ArrayList<>();
    private ArrayAdapter<String> adaptador;
    private int divisionseleccionado = -1;
    private Object mActionMode;
    private TextView pie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_division);
        pie = findViewById(R.id.id_division_pie);
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
                divisionseleccionado = position;
                mActionMode = Listar_Division.this.startActionMode(amc);
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
            ir_a_RegistrarDivision(null);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private ActionMode.Callback amc = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones_del_upd, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_eliminar) {
                AlertaEliminacion();
                mode.finish();
            } else if (item.getItemId() == R.id.item_modificar) {
                Division division = lista.get(divisionseleccionado);
                Intent in = new Intent(getApplicationContext(), Editar_Division.class);
                in.putExtra("iddivision", division.getIddivision());
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
        alertDialog.setMessage("¿Desea eliminar la clasificación seleccionada?");
        alertDialog.setTitle("Eliminar");
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                eliminarDivision();
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

    public void eliminarDivision(){
        try{
            Access_Division db = Access_Division.getInstance(getApplicationContext());
            Division division = lista.get(divisionseleccionado);
            db.openWritable();
            long resultado = db.EliminarDivision(division.getIddivision());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Clasificación eliminado satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista();
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al eliminar clasificación", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_division);
            Access_Division db = Access_Division.getInstance(getApplicationContext());
            Cursor c = db.getDivision();
            if (c.moveToFirst()){
                do {
                    lista.add( new Division (c.getInt(0),c.getString(1)));
                }while (c.moveToNext());
            }
            String[] arreglo = new String[lista.size()];
            for (int i = 0;i<arreglo.length;i++){
                arreglo[i] = lista.get(i).getDivision();
            }
            adaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_item_ld,arreglo);
            lv.setAdapter(adaptador);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" clasificación listada");
            }else{
                pie.setText(cant+" clasificaciones listadas");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void ir_a_RegistrarDivision(View view){
        Intent i = new Intent(getApplicationContext(), Registrar_Division.class);
        startActivity(i);
        finish();
    }
}