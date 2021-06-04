package com.example.latinodistribuidora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void irRegistrar(View view){
        Intent i = new Intent(this, Registrar.class);
        startActivity(i);
    }

    public void irMenu(View view){
        Intent i = new Intent(this, MenuPrincipal.class);
        startActivity(i);
    }

    public void irClientes(View view){
        Intent i = new Intent(this, Clientes.class);
        startActivity(i);

    }

}