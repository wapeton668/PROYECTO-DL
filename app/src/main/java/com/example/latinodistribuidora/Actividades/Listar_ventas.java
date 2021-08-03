package com.example.latinodistribuidora.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.latinodistribuidora.Adaptador_ventas;
import com.example.latinodistribuidora.CRUD.Access_Productos;
import com.example.latinodistribuidora.CRUD.Access_Venta;
import com.example.latinodistribuidora.Modelos.DetalleVenta;
import com.example.latinodistribuidora.Modelos.DetalleVentaSync;
import com.example.latinodistribuidora.Modelos.Ventas;
import com.example.latinodistribuidora.R;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Listar_ventas extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Ventas> lista = new ArrayList<>();
    private ArrayList<DetalleVentaSync> listaDetalle = new ArrayList<>();
    private Adaptador_ventas adaptadorVenta;
    private int ventaseleccionado = -1;
    private Object mActionMode;
    private TextView pie,totalvf;
    private String fechaactual;
    private EditText etFecha;
    private int ultimoAnio, ultimoMes, ultimoDiaDelMes;
    Button btnSync;

    // Crear un listener del datepicker;
    private final DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {
            // Esto se llama cuando seleccionan una fecha. Nos pasa la vista, pero más importante, nos pasa:
            // El año, el mes y el día del mes. Es lo que necesitamos para saber la fecha completa


            // Refrescamos las globales
            ultimoAnio = anio;
            ultimoMes = mes;
            ultimoDiaDelMes = diaDelMes;

            // Y refrescamos la fecha
            refrescarFechaEnEditText();

        }
    };

    public void refrescarFechaEnEditText() {
        // Formateamos la fecha pero podríamos hacer cualquier otra cosa ;)
        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%02d", ultimoDiaDelMes,ultimoMes+1,ultimoAnio);
        // La ponemos en el editText
        etFecha.setText(fecha);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_ventas);
        pie = findViewById(R.id.id_ventas_pie);
        totalvf = findViewById(R.id.txtTotalVR);
        etFecha = findViewById(R.id.etFecha);
        btnSync = findViewById(R.id.btnSync);
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        fechaactual = df.format(fecha);

        // Poner último año, mes y día a la fecha de hoy

        final Calendar calendario = Calendar.getInstance();
        ultimoAnio = calendario.get(Calendar.YEAR);
        ultimoMes = calendario.get(Calendar.MONTH);
        ultimoDiaDelMes = calendario.get(Calendar.DAY_OF_MONTH);

        // Refrescar la fecha en el EditText
        refrescarFechaEnEditText();

        // Hacer que el datepicker se muestre cuando toquen el EditText; recuerda
        // que se podría invocar en el click de cualquier otro botón, o en cualquier
        // otro evento

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí es cuando dan click así que mostramos el DatePicker

                // Le pasamos lo que haya en las globales
                DatePickerDialog dialogoFecha = new DatePickerDialog(Listar_ventas.this, listenerDeDatePicker, ultimoAnio, ultimoMes, ultimoDiaDelMes);
                //Mostrar
                dialogoFecha.show();
            }
        });


        llenarLista(etFecha.getText().toString());
        llenarListaDetalle();
        MostrarTotal(etFecha.getText().toString());
        onClick();

        etFecha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lista.removeAll(lista);
                llenarLista(s.toString());
                MostrarTotal(etFecha.getText().toString());
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncVentas();
            }
        });

    }

    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ventaseleccionado = position;
                mActionMode = Listar_ventas.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });
    }

    private ActionMode.Callback amc = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones_del, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_eliminar) {
                AlertaEliminacion();
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }


    };

    private void AlertaEliminacion(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Desea Anular la venta seleccionada?");
        alertDialog.setTitle("Anular");
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Anular", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                eliminarVenta();
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

    public void eliminarVenta(){
        try{
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            Ventas ventas = lista.get(ventaseleccionado);
            db.openWritable();
            long resultado = db.EliminarVenta(ventas.getId());
            if(resultado > 0){
                Toast.makeText(getApplicationContext(),"Venta anulada satisfactoriamente", Toast.LENGTH_LONG).show();
                lista.removeAll(lista);
                llenarLista(etFecha.getText().toString());
                MostrarTotal(etFecha.getText().toString());
            }else{
                Toast.makeText(getApplicationContext(),"Se produjo un error al anular la venta", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error Fatal: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(String fechaactual){
        try{
            lv = (ListView) findViewById(R.id.lv_listarventas);
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            db.openWritable();
            Cursor c = db.getv_venta(fechaactual);
            if (c.moveToFirst()){
                do {
                    lista.add( new Ventas(c.getInt(0),c.getInt(13),c.getInt(14),c.getInt(15),
                            c.getInt(16), c.getString(2), c.getString(3), c.getString(4),
                            c.getString(6), c.getString(7), c.getString(8), c.getString(9),c.getString(11),
                            c.getString(12),c.getString(18),c.getString(19),c.getInt(10),
                            c.getInt(17),c.getInt(5),c.getInt(1)));
                }while (c.moveToNext());
            }
            adaptadorVenta = new Adaptador_ventas(this,lista);
            //adaptadorVenta.notifyDataSetChanged();
            lv.setAdapter(adaptadorVenta);
            int cant = lv.getCount();
            if(cant==0){
                pie.setText("Lista vacía");
            }else if(cant==1){
                pie.setText(cant+" venta listada");
            }else{
                pie.setText(cant+" ventas listadas");
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void llenarListaDetalle(){
        try{
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            db.openWritable();
            Cursor c = db.getDetalleSync();
            if (c.moveToFirst()){
                do {
                    listaDetalle.add( new DetalleVentaSync(c.getInt(0),c.getInt(1),c.getInt(2),c.getString(3),
                            c.getInt(4),c.getInt(5),c.getInt(6),c.getString(7)));
                }while (c.moveToNext());
            }
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando listaDetalle: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void MostrarTotal(String fechaactual){
        Access_Venta db = Access_Venta.getInstance(getApplicationContext());
        db.openWritable();
        Cursor c = db.getTotal(fechaactual);
        if(c.moveToNext()){
            totalvf.setText("Recaudación: "+c.getInt(0));
        }else{
            totalvf.setText("Sin recaudación");
        }

    }

    public void SyncVentas() {
        if(lista.size()==0){
            Toast.makeText(this,"Los registros de ventas locales estan vacios",Toast.LENGTH_LONG).show();
        }else{
            JSONArray jsonArrayProducto = new JSONArray();
            for(int i = 0 ; i < lista.size() ; i++) {
                JSONObject jsonObjectProducto = new JSONObject();
                try {
                    jsonObjectProducto.put("idventas", lista.get(i).getId());
                    jsonObjectProducto.put("nrofactura", lista.get(i).getFactura());
                    jsonObjectProducto.put("condicion", lista.get(i).getCondicion());
                    jsonObjectProducto.put("fecha", lista.get(i).getFecha());
                    jsonObjectProducto.put("hora", lista.get(i).getHora());
                    jsonObjectProducto.put("total", lista.get(i).getTotal());
                    jsonObjectProducto.put("exenta", lista.get(i).getExenta());
                    jsonObjectProducto.put("iva5", lista.get(i).getIva5());
                    jsonObjectProducto.put("iva10", lista.get(i).getIva10());
                    jsonObjectProducto.put("estado", lista.get(i).getEstado());
                    jsonObjectProducto.put("cliente_idcliente", lista.get(i).getIdcliente());
                    jsonObjectProducto.put("usuario_idusuario", lista.get(i).getIdusuario());
                    jsonObjectProducto.put("idtimbrado", lista.get(i).getIdtimbrado());
                    jsonObjectProducto.put("idemision", lista.get(i).getIdemision());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArrayProducto.put(jsonObjectProducto);
            }
            JSONObject json = new JSONObject();
            try {
                json.put("Ventas", jsonArrayProducto);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonStr = json.toString();
            registrarVentas(jsonStr);
        }

    }

    public void SyncDetalles() {
            JSONArray jsonArrayDetalle = new JSONArray();
            for(int i = 0 ; i < listaDetalle.size() ; i++) {
                JSONObject jsonObjectDetalle = new JSONObject();
                try {
                    jsonObjectDetalle.put("venta_idventa", listaDetalle.get(i).getIdventa());
                    jsonObjectDetalle.put("idemision", listaDetalle.get(i).getIdemision());
                    jsonObjectDetalle.put("productos_idproductos", listaDetalle.get(i).getProductos_idproductos());
                    jsonObjectDetalle.put("cantidad", listaDetalle.get(i).getCantidad());
                    jsonObjectDetalle.put("precio", listaDetalle.get(i).getPrecio());
                    jsonObjectDetalle.put("total", listaDetalle.get(i).getTotal());
                    jsonObjectDetalle.put("impuesto_aplicado", listaDetalle.get(i).getImpuesto_aplicado());
                    jsonObjectDetalle.put("um", listaDetalle.get(i).getUm());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArrayDetalle.put(jsonObjectDetalle);
            }
            JSONObject json = new JSONObject();
            try {
                json.put("Detalles", jsonArrayDetalle);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonStr = json.toString();
            registrarDetalles(jsonStr);
    }

    public void registrarVentas(final String json) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_REGISTRAR_VENTAS), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("OK")) {
                    SyncDetalles();
                    Access_Venta db = Access_Venta.getInstance(getApplicationContext());
                    db.openWritable();
                    db.borrarVentas();
                    db.close();
                    lista.clear();
                    llenarLista(etFecha.getText().toString());
                    MostrarTotal(etFecha.getText().toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());
                String message = "";
                if (error instanceof NetworkError) {
                    message = "NetworkError: ¡Error de red!";
                } else if (error instanceof ServerError) {
                    message = "ServerError: No se pudo encontrar el servidor. ¡Inténtalo de nuevo después de un tiempo!";
                } else if (error instanceof ParseError) {
                    message = "ParseError: ¡Error de sintáxis! ¡Inténtalo de nuevo después de un tiempo!";
                } else if (error instanceof NoConnectionError) {
                    message = "NoConnectionError: No se puede conectar a Internet ... ¡Compruebe su conexión!";
                } else if (error instanceof TimeoutError) {
                    message = "TimeoutError: ¡El tiempo de conexión expiro! Por favor revise su conexion a internet.";
                }
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                Log.d(TAG, "jsArrayRequest Error : "+ message);
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("json", json);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void registrarDetalles(final String json) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_REGISTRAR_DETALLES), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("OK")) {
                    Access_Venta db = Access_Venta.getInstance(getApplicationContext());
                    db.openWritable();
                    db.borrarDetallesVenta();
                    db.close();
                    listaDetalle.clear();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());
                String message = "";
                if (error instanceof NetworkError) {
                    message = "NetworkError: ¡Error de red!";
                } else if (error instanceof ServerError) {
                    message = "ServerError: No se pudo encontrar el servidor. ¡Inténtalo de nuevo después de un tiempo!";
                } else if (error instanceof ParseError) {
                    message = "ParseError: ¡Error de sintáxis! ¡Inténtalo de nuevo después de un tiempo!";
                } else if (error instanceof NoConnectionError) {
                    message = "NoConnectionError: No se puede conectar a Internet ... ¡Compruebe su conexión!";
                } else if (error instanceof TimeoutError) {
                    message = "TimeoutError: ¡El tiempo de conexión expiro! Por favor revise su conexion a internet.";
                }
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                Log.d(TAG, "jsArrayRequest Error : "+ message);
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("json", json);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}