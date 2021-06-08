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

    public void irDepartamentos(View view){
        Intent i = new Intent(this, Listar_Departamento.class);
        startActivity(i);
    }

    public void irUsuarios(View v){
        Intent i = new Intent(getApplicationContext(), Listar_Usuario.class);
        startActivity(i);
    }
}