package com.example.latinodistribuidora.Actividades;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Clientes;
import com.example.latinodistribuidora.Adaptador_Productos_venta;
import com.example.latinodistribuidora.CRUD.Access_Productos;
import com.example.latinodistribuidora.CRUD.Access_UnicadMedida;
import com.example.latinodistribuidora.Modelos.Productos;
import com.example.latinodistribuidora.Modelos.UnidadMedida;
import com.example.latinodistribuidora.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.latinodistribuidora.Actividades.Editar_Producto.getIndexSpinnerUM;

public class Fragment_Venta extends Fragment {

    View vista;
    private ListView lv;
    private ArrayList<Productos> lista = new ArrayList<>();
    private Adaptador_Productos_venta adaptadorProductos;
    private int productoseleccionado = -1;
    private EditText buscar, txtCantidad;
    private int productoEditar;
    Button calcular;
    Spinner comboUM;
    ArrayList<String> listaum;
    ArrayList<UnidadMedida> umlist;
    public static int exenta,iva5,iva10;


    private TextView txtidproducto,txtproduto,txtprecio,txtumCant,txtumdescripcion,txtTotalVenta,txtiva,txttotaliva;

    public Fragment_Venta() {
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
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment__venta, container, false);
        buscar = vista.findViewById(R.id.id_buscarproductosV);
        txtidproducto = vista.findViewById(R.id.id_idprodven);
        txtproduto = vista.findViewById(R.id.id_txtproducto);
        txtprecio = vista.findViewById(R.id.id_txtprecio);
        txtumCant = vista.findViewById(R.id.txtidumcant);
        txtumdescripcion = vista.findViewById(R.id.txtiddescripum);
        txtCantidad = vista.findViewById(R.id.id_txtcant);
        txtTotalVenta = vista.findViewById(R.id.id_txttotal);
        txtiva = vista.findViewById(R.id.txt_idiva);
        txttotaliva = (TextView) vista.findViewById(R.id.txt_totaliva);
        comboUM = vista.findViewById(R.id.spinner_umv);
        calcular = (Button) vista.findViewById(R.id.btnCalcular);

        return vista;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull final View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llenarLista();
        onClick();

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filtro= String.valueOf(s.toString());
                if(filtro.length()>=0){
                    Log.i("",filtro);
                    lista.removeAll(lista);
                    llenarListaFiltrada(filtro);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        consultarlistaUM();
        ArrayAdapter<String> adaptadorUM = new ArrayAdapter(getContext(), R.layout.spinner_item_ldventa,listaum);
        comboUM.setAdapter(adaptadorUM);
        //int indexUM= getIndexSpinnerUM(comboUM, txtumdescripcion.getText().toString());
        //comboUM.setSelection(indexUM);
        comboUM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    if(position!=0){
                        txtumCant.setText(String.valueOf(umlist.get(position-1).getCant()));
                    }else{
                        txtumCant.setText("0");
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtidproducto.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(),"No ha seleccionado ningún producto",Toast.LENGTH_SHORT).show();
                }else if(txtCantidad.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(),"Ingrese cantidad",Toast.LENGTH_SHORT).show();
                    txtCantidad.requestFocus();
                }else if(Float.parseFloat(txtCantidad.getText().toString())<=0){
                    Toast.makeText(getContext(),"Ingrese cantidad válida",Toast.LENGTH_SHORT).show();
                    txtCantidad.requestFocus();
                }else{
                    int cantum = Integer.parseInt(txtumCant.getText().toString());
                    Float cantventa = Float.parseFloat(txtCantidad.getText().toString());
                    int precioventa = Integer.parseInt(txtprecio.getText().toString());
                    int total= (int) ((cantum*cantventa)*precioventa);
                    txtTotalVenta.setText(String.valueOf(total));
                    int imp=Integer.parseInt(((TextView) vista.findViewById(R.id.txt_idiva)).getText().toString());
                    switch (imp){
                        case 0:
                            exenta=0;
                            iva5=0;
                            iva10=0;
                            txttotaliva.setText(String.valueOf(exenta));
                            break;
                        case 5:
                            exenta=0;
                            iva5=total/21;
                            iva10=0;
                            txttotaliva.setText(String.valueOf(iva5));
                            break;
                        case 10:
                            exenta=0;
                            iva5=0;
                            iva10=total/11;
                            txttotaliva.setText(String.valueOf(iva10));
                            break;
                    }

                }
            }
        });

        ((Button)view.findViewById(R.id.btnEnviar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextView) view.findViewById(R.id.id_txttotal)).getText().toString().trim().isEmpty()){
                    ((Button) view.findViewById(R.id.btnCalcular)).requestFocusFromTouch();
                    Toast.makeText(getActivity(),"Falta calcular el total de esta venta",Toast.LENGTH_SHORT).show();
                }else{
                    String id=((TextView) view.findViewById(R.id.id_idprodven)).getText().toString();
                    String producto=((TextView  ) view.findViewById(R.id.id_txtproducto)).getText().toString();
                    String precio = ((TextView) view.findViewById(R.id.id_txtprecio)).getText().toString();
                    String cantidad = ((EditText) view.findViewById(R.id.id_txtcant)).getText().toString();
                    String um= ((Spinner) view.findViewById(R.id.spinner_umv)).getSelectedItem().toString();
                    int total= Integer.parseInt(((TextView) view.findViewById(R.id.id_txttotal)).getText().toString());
                    String ivadescripcion = ((TextView) view.findViewById(R.id.txt_idiva)).getText().toString();

                    Bundle enviar = new Bundle();
                    enviar.putString("id",id);
                    enviar.putString("producto",producto);
                    enviar.putString("precio",precio);
                    enviar.putString("cantidad",cantidad);
                    enviar.putString("um",um);
                    enviar.putInt("total",total);
                    enviar.putString("ivadescripcion",ivadescripcion);
                    enviar.putInt("exenta",exenta);
                    enviar.putInt("iva5",iva5);
                    enviar.putInt("iva10",iva10);

                    getParentFragmentManager().setFragmentResult("enviar",enviar);
                    Toast.makeText(getActivity(),"Venta cargada",Toast.LENGTH_SHORT).show();
                    limpiar();
                    buscar.requestFocusFromTouch();
                }
            }
        });

    }
    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                productoseleccionado = position;
                Productos productos = lista.get(productoseleccionado);
                txtidproducto.setText(String.valueOf(productos.getIdproducto()));
                txtproduto.setText(productos.getDescripcion());
                txtprecio.setText(productos.getPrecio());
                txtumdescripcion.setText(productos.getUnidad());
                txtiva.setText(String.valueOf(productos.getImpuesto()));
                int indexUM= getIndexSpinnerUM(comboUM, txtumdescripcion.getText().toString());
                comboUM.setSelection(indexUM);
                view.setSelected(true);
                txtCantidad.requestFocus();
                return true;
            }
        });
    }

    public void llenarLista(){
        try{
            lv = (ListView) vista.findViewById(R.id.list_prodavender);
            Access_Productos db = Access_Productos.getInstance(getContext());
            Cursor c = db.getProductos();
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(8),
                            c.getString(9),c.getString(6),c.getInt(7)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos_venta(getContext(),lista);
            lv.setAdapter(adaptadorProductos);
            db.close();

        }catch (Exception e){
            Toast.makeText(getContext(), "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void llenarListaFiltrada(String filtro){
        try{
            lv = (ListView) vista.findViewById(R.id.list_prodavender);
            Access_Productos db = Access_Productos.getInstance(getContext());
            Cursor c = db.getFiltrarProductos(filtro);
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(8),
                            c.getString(9),c.getString(6),c.getInt(7)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos_venta(getContext(),lista);
            lv.setAdapter(adaptadorProductos);
            db.close();

        }catch (Exception e){
            Toast.makeText(getContext(), "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void consultarlistaUM() {
        Access_UnicadMedida db = Access_UnicadMedida.getInstance(getContext());
        db.openReadable();
        UnidadMedida unidadMedida=null;
        umlist = new ArrayList<UnidadMedida>();
        Cursor cursor = db.getUnidadMedida();
        while (cursor.moveToNext()){
            unidadMedida = new UnidadMedida();
            unidadMedida.setIdunidad(cursor.getInt(0));
            unidadMedida.setUm(cursor.getString(1));
            unidadMedida.setCant(cursor.getInt(2));

            umlist.add(unidadMedida);
        }
        obtenerlistaUM();
    }
    private void obtenerlistaUM(){
        listaum = new ArrayList<String>();
        listaum.add("Selec.");
        for(int i=0; i < umlist.size();i++) {
            listaum.add(umlist.get(i).getUm().toString());
        }
    }

    private void limpiar(){
        txtidproducto.setText("");
        txtproduto.setText("");
        txtprecio.setText("");
        txtumdescripcion.setText("");
        txtCantidad.setText("");
        txtTotalVenta.setText("");
    }

}