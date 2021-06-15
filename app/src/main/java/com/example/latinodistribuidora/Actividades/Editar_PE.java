package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.CRUD.Access_PE;
import com.example.latinodistribuidora.CRUD.Access_Timbrado;
import com.example.latinodistribuidora.R;

public class Editar_PE extends AppCompatActivity {
    private int peEditar;
    private EditText establecimiento,pe,direccion, desde, hasta;
    private TextView est;
    public Switch switchE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pe);
        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            peEditar = extras.getInt("idemision");
        }
        establecimiento = findViewById(R.id.id_estable_mod);
        pe = findViewById(R.id.id_pe_mod);
        direccion = findViewById(R.id.id_dir_mod);
        desde = findViewById(R.id.id_inicio_mod);
        hasta = findViewById(R.id.id_fin_mod);
        est = findViewById(R.id.id_est_pe_mod);
        switchE = findViewById(R.id.id_switchPE_mod);

        reflejarCampos();
        if(est.getText().toString().equals("Activo")){
            switchE.setChecked(true);
            est.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }else{
            switchE.setChecked(false);
            est.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    public void onClick(View view){
        if(view.getId()==R.id.id_switchPE_mod){
            if(switchE.isChecked()){
                est.setText("Activo");
                est.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }else{
                est.setText("Inactivo");
                est.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }
    }

    public void reflejarCampos(){
        Access_PE db = Access_PE.getInstance(getApplicationContext());
        db.openReadable();
        Cursor c = db.getPE_a_Modificar(peEditar);
        try {
            if(c.moveToNext()){
                establecimiento.setText(c.getString(1));
                establecimiento.requestFocus();
                pe.setText(c.getString(2));
                direccion.setText(c.getString(3));
                desde.setText(c.getString(4));
                hasta.setText(c.getString(5));
                est.setText(c.getString(6));

            }
            db.close();
        }finally {}
    }
    public void Modificar(View v){
        if(establecimiento.getText().toString().trim().isEmpty()){
            establecimiento.requestFocus();
        }else if(pe.getText().toString().trim().isEmpty()){
            pe.requestFocus();
        }else if(direccion.getText().toString().trim().isEmpty()){
            direccion.requestFocus();
        }else if(desde.getText().toString().trim().isEmpty()){
            desde.requestFocus();
        }else if(hasta.getText().toString().trim().isEmpty()){
            hasta.requestFocus();
        }else{
            Access_PE db = Access_PE.getInstance(getApplicationContext());
            db.openWritable();
            ContentValues values = new ContentValues();
            values.put("establecimiento",establecimiento.getText().toString());
            values.put("puntoemision",pe.getText().toString());
            values.put("direccion",direccion.getText().toString());
            values.put("facturainicio",desde.getText().toString());
            values.put("facturafin",hasta.getText().toString());
            values.put("estado",est.getText().toString());
            long respuesta =db.ActualizarPE(values,peEditar);
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
        Intent i = new Intent(getApplicationContext(), Listar_PE.class);
        startActivity(i);
        establecimiento.setText("");
        pe.setText("");
        direccion.setText("");
        desde.setText("");
        hasta.setText("");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Listar_PE.class);
        startActivity(i);
        finish();
    }
}