package com.example.latinodistribuidora.Actividades;


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
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.latinodistribuidora.CRUD.Access_Departamentos;
import com.example.latinodistribuidora.Modelos.Departamentos;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_Departamento extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Departamentos> lista = new ArrayList<>();
    private ArrayAdapter<String> adaptador;
    private int departamentoseleccionado = -1;
    private Object mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_departamento);
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
                departamentoseleccionado = position;
                mActionMode = Listar_Departamento.this.startActionMode(amc);
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
            ir_a_RegistrarDepartamentos(null);
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
                Departamentos departamentos = lista.get(departamentoseleccionado);
                Intent in = new Intent(getApplicationContext(), Editar_Departamento.class);
                in.putExtra("iddepartamento", departamentos.getIddepartamento());
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
        alertDialog.setMessage("¿Desea eliminar el departamento seleccionado?");
        alertDialog.setTitle("Eliminar");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                eliminarDepartamento();
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

    public void eliminarDepartamento(){
        try{
            Access_Departamentos db = Access_Departamentos.getInstance(getApplicationContext());
            Departamentos departamentos = lista.get(departamentoseleccionado);
            db.openWritable();
            long resultado = db.EliminarDepartamento(departamentos.getIddepartamento());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Departamento eliminado satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista();
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al eliminar departamento", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_departamento);
            Access_Departamentos db = Access_Departamentos.getInstance(getApplicationContext());
            Cursor c = db.getDepartamentos();
            if (c.moveToFirst()){
                do {
                    lista.add( new Departamentos (c.getInt(0),c.getString(1)));
                }while (c.moveToNext());
            }
            String[] arreglo = new String[lista.size()];
            for (int i = 0;i<arreglo.length;i++){
                arreglo[i] = "Departamento de: "+lista.get(i).getDepartamento();
            }
            adaptador = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arreglo);
            lv.setAdapter(adaptador);
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void ir_a_RegistrarDepartamentos(View view){
            Intent i = new Intent(getApplicationContext(), Registrar_Departamento.class);
            startActivity(i);
            finish();
    }
}