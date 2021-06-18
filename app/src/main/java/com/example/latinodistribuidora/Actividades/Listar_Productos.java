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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Productos;
import com.example.latinodistribuidora.CRUD.Access_Productos;
import com.example.latinodistribuidora.Modelos.Productos;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Listar_Productos extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Productos> lista = new ArrayList<>();
    //private ArrayAdapter<String> adaptador;
    private Adaptador_Productos adaptadorProductos;
    private int productoseleccionado = -1;
    private Object mActionMode;
    private TextView pie;
    private EditText buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_productos);

        pie = findViewById(R.id.id_productos_pie);
        buscar = findViewById(R.id.id_buscarproductos);

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
                productoseleccionado = position;
                mActionMode = Listar_Productos.this.startActionMode(amc);
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
            ir_a_RegistrarProducto(null);
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
                Productos productos = lista.get(productoseleccionado);
                Intent in = new Intent(getApplicationContext(), Editar_Producto.class);
                in.putExtra("idproducto", productos.getIdproducto());
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
        alertDialog.setMessage("¿Desea eliminar el producto seleccionado?");
        alertDialog.setTitle("Eliminar");
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                eliminarProducto();
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

    public void eliminarProducto(){
        try{
            Access_Productos db = Access_Productos.getInstance(getApplicationContext());
            Productos productos = lista.get(productoseleccionado);
            db.openWritable();
            long resultado = db.EliminarProducto(productos.getIdproducto());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Producto eliminado satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista();
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al eliminar producto", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_productos);
            Access_Productos db = Access_Productos.getInstance(getApplicationContext());
            Cursor c = db.getProductos();
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(7),
                            c.getString(8),c.getString(6)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos(this,lista);
            lv.setAdapter(adaptadorProductos);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" producto listado");
            }else{
                pie.setText(cant+" productos listados");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarListaFiltrada(String filtro){
        try{
            lv = (ListView) findViewById(R.id.id_lista_productos);
            Access_Productos db = Access_Productos.getInstance(getApplicationContext());
            Cursor c = db.getFiltrarProductos(filtro);
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(7),
                            c.getString(8),c.getString(6)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos(this,lista);
            lv.setAdapter(adaptadorProductos);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" producto listado");
            }else{
                pie.setText(cant+" productos listados");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void ir_a_RegistrarProducto(View view){
        Intent i = new Intent(getApplicationContext(), Registrar_Productos.class);
        startActivity(i);
        finish();
    }
}