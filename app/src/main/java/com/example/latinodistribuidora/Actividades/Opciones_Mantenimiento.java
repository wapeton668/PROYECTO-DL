package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.latinodistribuidora.R;

public class Opciones_Mantenimiento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_mantenimiento);
    }

    public void irListaDepartamentos(View view){
        Intent i = new Intent(this, Listar_Departamento.class);
        startActivity(i);
    }

    public void irListaUsuarios(View v){
        Intent i = new Intent(getApplicationContext(), Listar_Usuario.class);
        startActivity(i);
    }
    public void irListaCiudad(View view){
        Intent i = new Intent(getApplicationContext(), Listar_Ciudad.class);
        startActivity(i);
    }
    public void irListaEmpresa(View view){
        Intent i = new Intent(getApplicationContext(), Listar_Empresa.class);
        startActivity(i);
    }

    public void irListaDivision(View view){
        Intent i = new Intent(getApplicationContext(), Listar_Division.class);
        startActivity(i);
    }

    public void irListaIVA(View view){
        Intent i = new Intent(getApplicationContext(), Listar_IVA.class);
        startActivity(i);
    }

    public void irListaUM(View view){
        Intent i = new Intent(getApplicationContext(), ListarUM.class);
        startActivity(i);
    }

    public void irListaTimbrado(View view){
        Intent i = new Intent(getApplicationContext(), Listar_Timbrado.class);
        startActivity(i);
    }
}