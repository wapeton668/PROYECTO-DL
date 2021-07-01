package com.example.latinodistribuidora.Actividades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Clientes;
import com.example.latinodistribuidora.Adaptador_Productos_detalleventa;
import com.example.latinodistribuidora.Adaptador_Productos_venta;
import com.example.latinodistribuidora.CRUD.Access_Productos;
import com.example.latinodistribuidora.Modelos.DetalleVenta;
import com.example.latinodistribuidora.Modelos.Productos;
import com.example.latinodistribuidora.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class Fragment_DetalleV extends Fragment {
    View vista;
    View convertView;
    private ListView lv;
    private ArrayList<DetalleVenta> lista = new ArrayList<>();
    private Adaptador_Productos_detalleventa adaptadorProductos;
    public static String producto,precio,cantidad,um,ivadescripcion;
    public static int id,total,exenta,iva5,iva10;
    public static int exentaT=0;
    public static int iva5T=0;
    public static int iva10T=0;
    public static int totalfinal=0;


    public Fragment_DetalleV() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment__detalle_v, container, false);
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull final View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("enviar", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                if(!result.isEmpty()){
                    id= Integer.parseInt(result.getString("id"));
                    producto=result.getString("producto");
                    precio=result.getString("precio");
                    cantidad=result.getString("cantidad");
                    um=result.getString("um");
                    total=result.getInt("total");
                    ivadescripcion=result.getString("ivadescripcion");
                    exenta= result.getInt("exenta");
                    iva5= result.getInt("iva5");
                    iva10 = result.getInt("iva10");
                    cargarItems();
                }
            }
        });
    }
    public void cargarItems(){
        try{
            lv = (ListView) vista.findViewById(R.id.id_listadetallev);
            lista.add( new DetalleVenta(id,producto,precio,cantidad,um,total,ivadescripcion,exenta,iva5,iva10));
            adaptadorProductos = new Adaptador_Productos_detalleventa(getContext(),lista);
            adaptadorProductos.notifyDataSetChanged();
            lv.setAdapter(adaptadorProductos);
            calcularExenta();
            calcular5();
            calcular10();
            calculartotalventa();
        }catch (Exception e){
            Toast.makeText(getContext(), "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void calcularExenta(){

        exentaT= exentaT+exenta;
        ((TextView)vista.findViewById(R.id.idtotalexenta)).setText(String.valueOf(exentaT));
    }
    public void calcular5(){
        iva5T=iva5T+iva5;
        ((TextView)vista.findViewById(R.id.idtotal5)).setText(String.valueOf(iva5T));
    }
    public void calcular10(){
        iva10T=iva10T+iva10;
        ((TextView)vista.findViewById(R.id.idtotal10)).setText(String.valueOf(iva10T));
    }
    public void calculartotalventa(){
        totalfinal=totalfinal+total;
        ((TextView)vista.findViewById(R.id.idtotalfinal)).setText(String.valueOf(totalfinal));
    }


}