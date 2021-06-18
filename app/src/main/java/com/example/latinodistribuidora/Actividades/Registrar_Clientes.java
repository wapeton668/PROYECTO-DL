package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.CRUD.Access_Ciudades;
import com.example.latinodistribuidora.CRUD.Access_Clientes;
import com.example.latinodistribuidora.CRUD.Access_Departamentos;
import com.example.latinodistribuidora.Modelos.Ciudades;
import com.example.latinodistribuidora.Modelos.Departamentos;
import com.example.latinodistribuidora.R;

import java.util.ArrayList;

public class Registrar_Clientes extends AppCompatActivity {
    Spinner comboCiudades;
    TextView txtrazonsocial, txtruc, txtdireccion, txttelefono, txtidciudad;
    ArrayList<String> listaciu;
    ArrayList<Ciudades> ciulist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_clientes);
        comboCiudades = findViewById(R.id.id_spinner_cl_crear);
        txtrazonsocial = findViewById(R.id.id_rz_crear);
        txtruc = findViewById(R.id.id_ruc_crear);
        txtdireccion = findViewById(R.id.id_dire_crear);
        txttelefono = findViewById(R.id.id_telef_crear);
        txtidciudad= findViewById(R.id.id_idciudadcrear);
        txtrazonsocial.requestFocus();
        consultarlistaciudades();
        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this, R.layout.spinner_item_ld,listaciu);
        comboCiudades.setAdapter(adaptador);
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
            Log.i("ciudad:", ciudades.getCiudad().toString());

            ciulist.add(ciudades);
        }
        obtenerlista();
    }

    private void obtenerlista(){
        listaciu = new ArrayList<String>();
        listaciu.add("Seleccione una ciudad");
        for(int i=0; i < ciulist.size();i++){
            listaciu.add(ciulist.get(i).getCiudad().toString());
        }
    }

    public void Guardar(View view){
        if(txtrazonsocial.getText().toString().trim().isEmpty()){
            txtrazonsocial.requestFocus();
        }else if(txtruc.getText().toString().trim().isEmpty()){
            txtruc.requestFocus();
        }else if(txtdireccion.getText().toString().trim().isEmpty()){
            txtdireccion.requestFocus();
        }else if(txttelefono.getText().toString().trim().isEmpty()){
            txttelefono.requestFocus();
        }else if (txtidciudad.getText().toString().equals("0")){
            Toast.makeText(getApplicationContext(),"Seleccione una ciudad",Toast.LENGTH_SHORT).show();
        }else{
            Access_Clientes db = Access_Clientes.getInstance(getApplicationContext());
            db.openWritable();
            long insertado = db.insertarClientes(txtrazonsocial.getText().toString(),txtruc.getText().toString(),txtdireccion.getText().toString()
                    ,txttelefono.getText().toString(),Integer.parseInt(txtidciudad.getText().toString()));
            db.close();
            if(insertado>0){
                Toast.makeText(getApplicationContext(),"Cliente registrado exitosamente",Toast.LENGTH_SHORT).show();
                Limpiar_y_volver_a_Principal();
            }else{
                Toast.makeText(getApplicationContext(),"No se pudo registra la cliente",Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void Limpiar_y_volver_a_Principal(){
        txtrazonsocial.setText("");
        txtruc.setText("");
        txtdireccion.setText("");
        txttelefono.setText("");
        comboCiudades.setSelection(0);
        txtidciudad.setText("0");

        Intent i = new Intent(getApplicationContext(), Listar_Clientes.class);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Listar_Clientes.class);
        startActivity(i);
    }
}