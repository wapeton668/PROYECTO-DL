package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.R;

public class Registrar_venta extends AppCompatActivity{
    private int idCliente;
    private String razonsocial,ruc,fecha,vendedor,idvendedor,establecimiento,puntoexpdicion,facturaA;
    private TextView txtrazonsocial,txtruc,txtidcliente,
            txtfecha, txtidvendedor, txtvendedor,txtestablecimiento,txtpuntoexpedicion,txtfacturaA;

    Fragment_Venta fragment_venta;
    Fragment_DetalleV fragment_detalleV;
    Fragment_FinalV fragment_finalV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_venta1);
        txtrazonsocial= findViewById(R.id.id_DescCliente);
        txtruc= findViewById(R.id.id_ruc_venta);
        txtidcliente= findViewById(R.id.id_Cliente);
        txtfecha= findViewById(R.id.id_Fecha);
        txtidvendedor= findViewById(R.id.id_Vendedor);
        txtvendedor= findViewById(R.id.id_NombVendedor);
        txtestablecimiento = findViewById(R.id.id_estab);
        txtpuntoexpedicion = findViewById(R.id.id_pexp);
        txtfacturaA = findViewById(R.id.id_nFact);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            idCliente = extras.getInt("idcliente");
            razonsocial= extras.getString("razonsocial");
            ruc= extras.getString("ruc");
            fecha= extras.getString("fecha");
            idvendedor= extras.getString("idvendedor");
            vendedor= extras.getString("vendedor");
            establecimiento= extras.getString("establecimiento");
            puntoexpdicion= extras.getString("puntoexpedicion");
            facturaA = extras.getString("facturaactual");

            txtestablecimiento.setText(establecimiento);
            txtpuntoexpedicion.setText(puntoexpdicion);
            txtrazonsocial.setText(razonsocial);
            txtruc.setText(ruc);
            txtidcliente.setText(String.valueOf(idCliente));
            txtfecha.setText(fecha);
            txtidvendedor.setText(idvendedor);
            txtvendedor.setText(vendedor);
            txtfacturaA.setText(facturaA);
        }

        fragment_venta = new Fragment_Venta();
        fragment_detalleV = new Fragment_DetalleV();
        fragment_finalV = new Fragment_FinalV();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmen, fragment_detalleV).hide(fragment_detalleV).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmen, fragment_finalV).hide(fragment_finalV).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmen, fragment_venta).commit();

    }

    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.btnCVenta:
                if (fragment_venta.isAdded()) {
                    fragmentTransaction
                            .show(fragment_venta)
                            .hide(fragment_detalleV)
                            .hide(fragment_finalV);

                } else {
                    fragmentTransaction
                            .add(R.id.contenedorFragmen, fragment_venta)
                            .hide(fragment_detalleV)
                            .hide(fragment_finalV);

                }

                //fragmentTransaction.replace(R.id.contenedorFragmen,fragment_venta);
                break;
            case R.id.btnDVenta:
                if (fragment_detalleV.isAdded()) {
                    fragmentTransaction
                            .hide(fragment_venta)
                            .show(fragment_detalleV)
                            .hide(fragment_finalV);

                } else {
                    fragmentTransaction
                            .hide(fragment_venta)
                            .add(R.id.contenedorFragmen, fragment_detalleV)
                            .hide(fragment_finalV);

                }
                //fragmentTransaction.replace(R.id.contenedorFragmen,fragment_detalleV);
                break;
            case R.id.btnFVenta:
                if (fragment_finalV.isAdded()) {
                    fragmentTransaction
                            .hide(fragment_venta)
                            .hide(fragment_detalleV)
                            .show(fragment_finalV);
                } else {
                    fragmentTransaction
                            .hide(fragment_venta)
                            .hide(fragment_detalleV)
                            .add(R.id.contenedorFragmen, fragment_finalV);
                    //fragmentTransaction.addToBackStack(null);
                }
                //fragmentTransaction.replace(R.id.contenedorFragmen,fragment_finalV);
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertaCerrar();

    }

    private void AlertaCerrar(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Seguro que desea cancelar la venta?");
        alertDialog.setTitle("Cancelar");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Sí, cancelar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        alertDialog.setNegativeButton("Continuar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}