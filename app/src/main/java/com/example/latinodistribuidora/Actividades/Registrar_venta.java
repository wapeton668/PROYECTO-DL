package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.latinodistribuidora.R;

public class Registrar_venta extends AppCompatActivity {

    Fragment_Venta fragment_venta;
    Fragment_DetalleV fragment_detalleV;
    Fragment_FinalV fragment_finalV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_venta);
        fragment_venta = new Fragment_Venta();
        fragment_detalleV = new Fragment_DetalleV();
        fragment_finalV = new Fragment_FinalV();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmen, fragment_venta).commit();
    }

    public void onClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (view.getId()){
            case R.id.btnCVenta:
                transaction.replace(R.id.contenedorFragmen, fragment_venta);
                break;
            case R.id.btnDVenta:
                transaction.replace(R.id.contenedorFragmen, fragment_detalleV);
                break;
            case R.id.btnFVenta:
                transaction.replace(R.id.contenedorFragmen, fragment_finalV);
                break;
        }


        transaction.commit();
    }
}