package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Clientes;
import com.example.latinodistribuidora.Adaptador_Productos;
import com.example.latinodistribuidora.CRUD.Access_Clientes;
import com.example.latinodistribuidora.Modelos.Clientes;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_Clientes extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Clientes> lista = new ArrayList<>();
    private Adaptador_Clientes adaptadorClientes;
   // private ArrayAdapter<String> adaptador;
    private int clienteseleccionado = -1;
    private Object mActionMode;
    private TextView pie;
    private EditText buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_clientes);
        pie = findViewById(R.id.id_cli_nue_pie);
        buscar = findViewById(R.id.id_buscarcliente);

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
                mActionMode = Listar_Clientes.this.startActionMode(amc);
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
            ir_a_RegistrarClientes(null);
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
                Clientes clientes = lista.get(clienteseleccionado);
                Intent in = new Intent(getApplicationContext(), Editar_Clientes.class);
                in.putExtra("idcliente", clientes.getIdcliente());
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
        alertDialog.setMessage("¿Desea eliminar el cliente seleccionado?");
        alertDialog.setTitle("Eliminar");
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                eliminarCliente();
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

    public void eliminarCliente(){
        try{
            Access_Clientes db = Access_Clientes.getInstance(getApplicationContext());
            Clientes clientes = lista.get(clienteseleccionado);
            db.openWritable();
            long resultado = db.EliminarCliente(clientes.getIdcliente());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Cliente eliminado satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista();
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al eliminar cliente", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_cliente);
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
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" cliente listado");
            }else{
                pie.setText(cant+" clientes listados");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarListaFiltrada(String filtro){
        try{
            lv = (ListView) findViewById(R.id.id_lista_cliente);
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
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" cliente listado");
            }else{
                pie.setText(cant+" clientes listados");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void ir_a_RegistrarClientes(View view){
        Intent i = new Intent(getApplicationContext(), Registrar_Clientes.class);
        startActivity(i);
        finish();
    }
}