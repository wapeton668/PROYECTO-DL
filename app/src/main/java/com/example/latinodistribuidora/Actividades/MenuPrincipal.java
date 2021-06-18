package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.latinodistribuidora.MainActivity;
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

    public void irClientes(View view){
        Intent i = new Intent( this, Listar_Clientes.class);
        startActivity(i);
    }

    public void irProductos(View view){
        Intent i = new Intent( this, Listar_Productos.class);
        startActivity(i);
    }

    public void CerrarSesion(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Esta seguro que desea cerrar sesión?");
        alertDialog.setTitle("Cerrar Sesión");
        alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent i = new Intent( getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Una sesión se encuentra iniciada.\nCierre Sesión si desea finalizar las operaciones.", Toast.LENGTH_SHORT).show();
    }
}