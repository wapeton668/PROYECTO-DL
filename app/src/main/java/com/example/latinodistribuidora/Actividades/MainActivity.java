package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.latinodistribuidora.CRUD.Access_Usuarios;
import com.example.latinodistribuidora.R;

public class MainActivity extends AppCompatActivity {
    public EditText usuario, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usuario = findViewById(R.id.id_iniciar_usuario);
        contrasena = findViewById(R.id.id_iniciar_contrasena);
    }

    public void irRegistrar(View view){
        Intent i = new Intent(this, Registrar_Usuario.class);
        startActivity(i);
        finish();
    }

    public void IniciarSesion (View view){
        if(usuario.getText().toString().trim().isEmpty()){
            usuario.requestFocus();
        }else if(contrasena.getText().toString().trim().isEmpty()){
            contrasena.requestFocus();
        }else{
            try{
                Access_Usuarios db = Access_Usuarios.getInstance(getApplicationContext());
                Cursor cursor= db.getUsuario_IniciarSesion(usuario.getText().toString(), contrasena.getText().toString());
                if(cursor.moveToNext()){
                    db.close();
                    Intent i = new Intent(this, MenuPrincipal.class);
                    startActivity(i);
                    this.finish();
                }else{
                    Toast.makeText(this, "Error de Logueo." +
                            "\nUsuario o contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                    usuario.requestFocus();
                }
            }catch(Exception e) {
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}