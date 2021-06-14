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

import com.example.latinodistribuidora.CRUD.Access_Empresa;
import com.example.latinodistribuidora.Modelos.Empresas;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_Empresa extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Empresas> lista = new ArrayList<>();
    private ArrayAdapter<String> adaptador;
    private int empresaseleccionado = -1;
    private Object mActionMode;
    private TextView pie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_empresa);
        pie = findViewById(R.id.id_emp_pie);
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
                empresaseleccionado = position;
                mActionMode = Listar_Empresa.this.startActionMode(amc);
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
            ir_a_RegistrarEmpresa(null);
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
            if (item.getItemId() == R.id.item_inactivar) {
                AlertaEliminacion();
                mode.finish();
            } else if (item.getItemId() == R.id.item_modificar) {
                    Empresas empresas = lista.get(empresaseleccionado);
                    Intent in = new Intent(getApplicationContext(), Editar_Empresa.class);
                    in.putExtra("idempresa", empresas.getIdempresa());
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
        alertDialog.setMessage("¿Desea eliminar la empresa seleccionada?");
        alertDialog.setTitle("Eliminar");
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                EliminarEmpresa();
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

    public void EliminarEmpresa(){
        try{
            Access_Empresa db = Access_Empresa.getInstance(getApplicationContext());
            Empresas empresas = lista.get(empresaseleccionado);
            //db.openWritable();
            //Toast.makeText(getApplicationContext(),empresas.getIdempresa(), Toast.LENGTH_LONG).show();
            long resultado = db.EliminarEmpresa(empresas.getIdempresa());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Empresa eliminada satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista();
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al eliminar empresa", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_empresas);
            Access_Empresa db = Access_Empresa.getInstance(getApplicationContext());
            Cursor c = db.getEmpresas();
            if (c.moveToFirst()){
                do {
                    lista.add( new Empresas (c.getInt(0),c.getString(1),
                            c.getString(2), c.getString(3), c.getString(4),
                            c.getString(6)));
                }while (c.moveToNext());
            }
            String[] arreglo = new String[lista.size()];
            for (int i = 0;i<arreglo.length;i++){
                arreglo[i] = "Razón Social: "+lista.get(i).getRazon_social()+"\nR.U.C.: "+lista.get(i).getRuc()+
                "\nDirección: "+lista.get(i).getDireccion()+"\nTelefono: "+lista.get(i).getTelefono()+
                "\nCiudad: "+lista.get(i).getCiudad();
            }
            adaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_item_ld,arreglo);
            lv.setAdapter(adaptador);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }
            if(cant<=1){
                pie.setText(cant+" empresa listada");
            }else{
                pie.setText(cant+" empresas listadas");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void ir_a_RegistrarEmpresa(View view){
        Intent i = new Intent(getApplicationContext(), Registrar_Empresa.class);
        startActivity(i);
        finish();
    }
}