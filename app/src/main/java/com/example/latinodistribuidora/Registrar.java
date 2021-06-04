package com.example.latinodistribuidora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.jar.JarEntry;

public class Registrar extends AppCompatActivity {
    public static EditText nombre;
    public static EditText ci;
    public static EditText direccion;
    public static EditText celular;
    public static EditText contrasena;
    public static EditText usuario;
    public Button btnRegistrar;
    public Button btnListar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        nombre = findViewById(R.id.id_nombre_registrar);
        ci = findViewById(R.id.id_cedula_registrar);
        direccion = findViewById(R.id.id_direccion_registrar);
        celular = findViewById(R.id.id_celular_registrar);
        contrasena = findViewById(R.id.id_contraseña_registrar);
        usuario = findViewById(R.id.id_usuario_registrar);

        btnRegistrar = findViewById(R.id.idguardar_registro);
        btnListar = findViewById(R.id.id_btnListarUsuarios);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
                    db.open();
                    db.insertarUsuario(nombre.getText().toString(), ci.getText().toString(), direccion.getText().toString(),
                            celular.getText().toString(), usuario.getText().toString(), contrasena.getText().toString());
                    db.close();
                    Toast.makeText(getApplicationContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

                }catch (Exception E){

                    Toast.makeText(getApplicationContext(), "Error: " + E.getMessage(), Toast.LENGTH_LONG).show();

                }


            }
        });
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), VistaUsuarios.class);
                startActivity(i);


            }
        });
    }
}