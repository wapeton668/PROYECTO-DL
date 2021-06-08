package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.latinodistribuidora.R;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
    }
/*
    public void irClientes(View view){
        Intent i = new Intent(this, Listar_Cliente.class);
        startActivity(i);
    }*/
    public void irOpcionesMantenimieto(View view){
        Intent i = new Intent( this, Opciones_Mantenimiento.class);
        startActivity(i);
    }
}