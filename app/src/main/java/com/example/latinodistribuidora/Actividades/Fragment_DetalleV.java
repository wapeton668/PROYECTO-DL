package com.example.latinodistribuidora.Actividades;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Productos_detalleventa;
import com.example.latinodistribuidora.CRUD.Access_Venta;
import com.example.latinodistribuidora.Modelos.DetalleVenta;
import com.example.latinodistribuidora.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment_DetalleV extends Fragment {
    View vista;
    private ListView lv;
    private ArrayList<DetalleVenta> lista = new ArrayList<>();
    private Adaptador_Productos_detalleventa adaptadorProductos;
    public static String producto,precio,cantidad,um,ivadescripcion;
    public static int id,total,exenta,iva5,iva10;
    public static int exentaT=0;
    public static int iva5T=0;
    public static int iva10T=0;
    public static int totalfinal=0;

    private static final String ARG_IDVENTA = "idventas";
    private static final String ARG_NROFACTURA = "nrofactura";
    private static final String ARG_CONDICION = "condicion";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_IDCLIENTE = "idcliente";
    private static final String ARG_IDUSUARIO = "idusuario";
    private static final String ARG_IDTIMBRADO = "idtimbrado";
    private static final String ARG_IDEMISION = "idemision";


    private int idventas;
    private String nrofactura;
    private String condicion;
    private String fecha;
    private int idcliente;
    private int idusuario;
    private int idtimbrado;
    private int idemision;



    public Fragment_DetalleV() {
        // Required empty public constructor
    }
    public static Fragment_DetalleV newInstance(int idventas, String nrofactura, String condicion, String fecha,
                                              int idcliente, int idusuario, int idtimbrado, int idemision) {
        Fragment_DetalleV fragment = new Fragment_DetalleV();
        Bundle args = new Bundle();
        args.putInt(ARG_IDVENTA, idventas);
        args.putString(ARG_NROFACTURA, nrofactura);
        args.putString(ARG_CONDICION, condicion);
        args.putString(ARG_FECHA, fecha);
        args.putInt(ARG_IDCLIENTE, idcliente);
        args.putInt(ARG_IDUSUARIO, idusuario);
        args.putInt(ARG_IDTIMBRADO, idtimbrado);
        args.putInt(ARG_IDEMISION, idemision);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idventas = getArguments().getInt(ARG_IDVENTA);
            nrofactura = getArguments().getString(ARG_NROFACTURA);
            condicion = getArguments().getString(ARG_CONDICION);
            fecha = getArguments().getString(ARG_FECHA);
            idcliente = getArguments().getInt(ARG_IDCLIENTE);
            idusuario = getArguments().getInt(ARG_IDUSUARIO);
            idtimbrado = getArguments().getInt(ARG_IDTIMBRADO);
            idemision = getArguments().getInt(ARG_IDEMISION);
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
        ((Button)view.findViewById(R.id.btn_idFVenta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardar();
                getActivity().finish();
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

    /*private void enviarDatosFragmentFinal(){
        Bundle datosF = new Bundle();
        datosF.putString("exenta",((TextView)vista.findViewById(R.id.idtotalexenta)).getText().toString());
        datosF.putString("iva5",((TextView)vista.findViewById(R.id.idtotal5)).getText().toString());
        datosF.putString("iva10",((TextView)vista.findViewById(R.id.idtotal10)).getText().toString());
        datosF.putString("total",((TextView)vista.findViewById(R.id.idtotalfinal)).getText().toString());

        getParentFragmentManager().setFragmentResult("datosF",datosF);
    }*/

    public void Guardar(){
        Access_Venta db = Access_Venta.getInstance(getContext());
        if(lista.size()==0){
            Toast.makeText(getContext(), "Lista vacía", Toast.LENGTH_SHORT).show();
        }else{
            db.openWritable();
            long insertarventa = db.insertarventa(idventas,nrofactura,condicion,fecha,
                    Integer.parseInt(((TextView)vista.findViewById(R.id.idtotalfinal)).getText().toString()),
                    Integer.parseInt(((TextView)vista.findViewById(R.id.idtotalexenta)).getText().toString())
                    ,Integer.parseInt(((TextView)vista.findViewById(R.id.idtotal5)).getText().toString()),
                    Integer.parseInt(((TextView)vista.findViewById(R.id.idtotal10)).getText().toString()),
                    idcliente,idusuario,idtimbrado,idemision);
            Log.i("venta: ", String.valueOf(insertarventa));
            if(insertarventa>0){
                Toast.makeText(getContext(),"Venta registrada!",Toast.LENGTH_SHORT).show();
                GuardarDetalleVenta();
            }else{
                Toast.makeText(getContext(),"No se pudo registra la venta",Toast.LENGTH_SHORT).show();
            }
        }
        db.close();
    }
    public void GuardarDetalleVenta(){
        Access_Venta db = Access_Venta.getInstance(getContext());
        db.openWritable();
        long insertarDetalle = 0;
            for(int i=1; i<=lista.size();i++){
                int position = (i-1);
                DetalleVenta item = lista.get(position);
                Toast.makeText(getContext(),"Detalle: \nid:"+item.getIdproducto()
                        +"\ncantidad: "+item.getCantidad()
                        +"\nPrecio: "+Integer.parseInt(item.getPrecio())
                        +"\niva: "+Integer.parseInt(item.getIvadescripcion()),Toast.LENGTH_LONG).show();
                try{
                    insertarDetalle = db.insertarDetalle(idventas,item.getIdproducto(),item.getCantidad(),
                            Integer.parseInt(item.getPrecio()),item.getTotal(),Integer.parseInt(item.getIvadescripcion()));
                    Log.i("detalle: ", String.valueOf(insertarDetalle));
                }catch(Exception e){
                    Toast.makeText(getContext(), "FATAL: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            db.close();
    }

}