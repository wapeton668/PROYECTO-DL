package com.example.latinodistribuidora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.latinodistribuidora.Modelos.Usuario;

import java.util.ArrayList;

public class VistaUsuarios extends AppCompatActivity {
    private ListView lv;
    ArrayList<String> lista;
    ArrayAdapter adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_usuarios);
        lv = (ListView) findViewById(R.id.lista);
        cargarUsuarios();
    }
    private void cargarUsuarios(){
        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        lista = db.getUsuarios();
        adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1,lista);
        lv.setAdapter(adaptador);
        db.close();

    }

}