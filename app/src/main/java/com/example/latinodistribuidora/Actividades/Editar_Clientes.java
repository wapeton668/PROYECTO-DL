package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.CRUD.Access_Ciudades;
import com.example.latinodistribuidora.CRUD.Access_Clientes;
import com.example.latinodistribuidora.Modelos.Ciudades;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Editar_Clientes extends AppCompatActivity {
    private int clienteEditar;
    private EditText txtrazonsocial,txtruc,txtdireccion,txttelefono;
    private TextView txtidciudad;
    private TextView txtciudad;

    Spinner comboCiudades;
    ArrayList<String> listaciu;
    ArrayList<Ciudades> ciulist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_clientes);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            clienteEditar = extras.getInt("idcliente");
        }
        txtrazonsocial = findViewById(R.id.id_rz_mod);
        txtruc = findViewById(R.id.id_ruc_mod);
        txtdireccion = findViewById(R.id.id_dire_mod);
        txttelefono = findViewById(R.id.id_telef_mod);

        comboCiudades = findViewById(R.id.id_spinner_cl_mod);
        txtidciudad = findViewById(R.id.id_txtidciudad);
        txtciudad = findViewById(R.id.id_txtciudad_mod);
        reflejarCampos();
        txtrazonsocial.requestFocus();
    }

    public void reflejarCampos(){
        Access_Clientes db = Access_Clientes.getInstance(getApplicationContext());
        db.openReadable();
        Cursor c = db.getCliente_a_modificar(clienteEditar);
        try {
            if(c.moveToNext()){
                txtrazonsocial.setText(c.getString(1));
                txtruc.setText(c.getString(2));
                txtdireccion.setText(c.getString(3));
                txttelefono.setText(c.getString(4));
                txtciudad.setText(c.getString(6));
                consultarlistaciudades();
                ArrayAdapter<String> adaptador = new ArrayAdapter(this, R.layout.spinner_item_ld,listaciu);
                comboCiudades.setAdapter(adaptador);
                int index= getIndexSpinner(comboCiudades, txtciudad.getText().toString());
                comboCiudades.setSelection(index);
                comboCiudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            if(position!=0){
                                txtidciudad.setText(String.valueOf(ciulist.get(position-1).getIdciudad()));
                            }else{
                                txtidciudad.setText("0");
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }finally {}
    }

    public void Modificar(View v){
        if(txtrazonsocial.getText().toString().trim().isEmpty()){
            txtrazonsocial.requestFocus();
        }else if(txtruc.getText().toString().trim().isEmpty()){
            txtruc.requestFocus();
        }else if(txtdireccion.getText().toString().trim().isEmpty()){
            txtdireccion.requestFocus();
        }else if(txttelefono.getText().toString().trim().isEmpty()){
            txttelefono.requestFocus();
        }else if(txtidciudad.getText().toString().equals("0")){
            Toast.makeText(getApplicationContext(),"Seleccione una ciudad",Toast.LENGTH_SHORT).show();
        }else{
            Access_Clientes db = Access_Clientes.getInstance(getApplicationContext());
            db.openWritable();
            ContentValues values = new ContentValues();
            values.put("razon_social",txtrazonsocial.getText().toString());
            values.put("ruc",txtruc.getText().toString());
            values.put("direccion",txtdireccion.getText().toString());
            values.put("telefono",txttelefono.getText().toString());
            values.put("ciudad_idciudad", txtidciudad.getText().toString());
            long respuesta =db.ActualizarCliente(values, clienteEditar);
            db.close();
            if(respuesta >0){
                Toast.makeText(this, "Modificación realizada exitosamente", Toast.LENGTH_LONG).show();
                Limpiar_y_volver_a_Lista();
            }else{
                Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void Limpiar_y_volver_a_Lista(){
        Intent i = new Intent(getApplicationContext(), Listar_Clientes.class);
        startActivity(i);
        txtrazonsocial.setText("");
        txtruc.setText("");
        txtdireccion.setText("");
        txttelefono.setText("");
        comboCiudades.setSelection(0);
        txtidciudad.setText("0");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Listar_Clientes.class);
        startActivity(i);
        finish();
    }

    private void consultarlistaciudades() {
        Access_Ciudades db = Access_Ciudades.getInstance(getApplicationContext());
        db.openReadable();
        Ciudades ciudades=null;
        ciulist = new ArrayList<Ciudades>();
        Cursor cursor = db.getCiudades();
        while (cursor.moveToNext()){
            ciudades = new Ciudades();
            ciudades.setIdciudad(cursor.getInt(0));
            ciudades.setCiudad(cursor.getString(1));

            Log.i("id:", String.valueOf(ciudades.getIdciudad()));
            Log.i("ciudad:",ciudades.getCiudad().toString());

            ciulist.add(ciudades);
        }
        obtenerlista();
    }

    private void obtenerlista(){
        listaciu = new ArrayList<String>();
        listaciu.add("Seleccione una ciudad");
        for(int i=0; i < ciulist.size();i++) {
            listaciu.add(ciulist.get(i).getCiudad().toString());
        }
    }

    public static int getIndexSpinner(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
            }
        }
        return index;
    }
}