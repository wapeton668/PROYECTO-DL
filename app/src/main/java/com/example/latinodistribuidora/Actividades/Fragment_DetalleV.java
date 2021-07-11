package com.example.latinodistribuidora.Actividades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latinodistribuidora.Adaptador_Productos_detalleventa;
import com.example.latinodistribuidora.CRUD.Access_Empresa;
import com.example.latinodistribuidora.CRUD.Access_Timbrado;
import com.example.latinodistribuidora.CRUD.Access_Venta;
import com.example.latinodistribuidora.Modelos.DetalleVenta;
import com.example.latinodistribuidora.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
public class Fragment_DetalleV extends Fragment {
    View vista;
    View vista2;

    private static final String ARG_IDVENTA = "idventas";
    private static final String ARG_NESTABLECIMIENTO = "nestablecimiento";
    private static final String ARG_NEXPEDICION = "nemision";
    private static final String ARG_NROFACTURA = "nrofactura";
    private static final String ARG_CONDICION = "condicion";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_IDCLIENTE = "idcliente";
    private static final String ARG_CRZ = "Vcliente";
    private static final String ARG_CRUC = "Vruc";
    private static final String ARG_IDUSUARIO = "idusuario";
    private static final String ARG_VENDEDOR = "vendedor";
    private static final String ARG_IDTIMBRADO = "idtimbrado";
    private static final String ARG_IDEMISION = "idemision";

    private ListView lv;
    private ArrayList<DetalleVenta> lista = new ArrayList<>();
    private Adaptador_Productos_detalleventa adaptadorProductos;



    public static String producto,precio,cantidad,um,ivadescripcion;
    public static int id,total,exenta,iva5,iva10;
    public static int exentaT;
    public static int iva5T;
    public static int iva10T;
    public static int totalfinal;


    private int idventas;
    private String nestablecimiento;
    private String nemision;
    private String nrofactura;
    private String condicion;
    private String fecha;
    private int idcliente;
    private String vcliente;
    private String vruccliente;
    private int idusuario;
    private String vendedor;
    private int idtimbrado;
    private int idemision;

    private int BrutoExenta=0;
    private int Bruto5=0;
    private int Bruto10=0;


    String timbActual, fdesde,fhasta;
    String empRZ, empRUC, empDir, empTel,empCiu;


    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    TextView myLabel;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;



    public Fragment_DetalleV() {
        // Required empty public constructor

    }
    public static Fragment_DetalleV newInstance(int idventas,String nestablecimiento,String nemision, String nrofactura, String condicion, String fecha,
                                              int idcliente,String cliente,String ruccliente, int idusuario,String vendedor, int idtimbrado, int idemision) {
        Fragment_DetalleV fragment = new Fragment_DetalleV();
        Bundle args = new Bundle();
        args.putInt(ARG_IDVENTA, idventas);
        args.putString(ARG_NESTABLECIMIENTO, nestablecimiento);
        args.putString(ARG_NEXPEDICION, nemision);
        args.putString(ARG_NROFACTURA, nrofactura);
        args.putString(ARG_CONDICION, condicion);
        args.putString(ARG_FECHA, fecha);
        args.putInt(ARG_IDCLIENTE, idcliente);
        args.putString(ARG_CRZ, cliente);
        args.putString(ARG_CRUC, ruccliente);
        args.putInt(ARG_IDUSUARIO, idusuario);
        args.putString(ARG_VENDEDOR, vendedor);
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
            nestablecimiento = getArguments().getString(ARG_NESTABLECIMIENTO);
            nemision = getArguments().getString(ARG_NEXPEDICION);
            nrofactura = getArguments().getString(ARG_NROFACTURA);
            condicion = getArguments().getString(ARG_CONDICION);
            fecha = getArguments().getString(ARG_FECHA);
            idcliente = getArguments().getInt(ARG_IDCLIENTE);
            vcliente = getArguments().getString(ARG_CRZ);
            vruccliente = getArguments().getString(ARG_CRUC);
            idusuario = getArguments().getInt(ARG_IDUSUARIO);
            vendedor = getArguments().getString(ARG_VENDEDOR);
            idtimbrado = getArguments().getInt(ARG_IDTIMBRADO);
            idemision = getArguments().getInt(ARG_IDEMISION);

            obtenerEmpresa();
            obtenerTimbrado();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment__detalle_v, container, false);
        myLabel = (TextView) vista.findViewById(R.id.myLabel);
        lv = (ListView) vista.findViewById(R.id.id_listadetallev);
        exentaT=0;
        iva5T=0;
        iva10T=0;
        totalfinal=0;
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
                AlertaRegistrar();
            }

        });

        ((ImageButton) view.findViewById(R.id.btnBT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    findBT();
                    openBT();
                }catch (Exception i){}
            }
        });

        ((Button)view.findViewById(R.id.btnImprimir)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                        sendData();
                    ((Button)view.findViewById(R.id.btnImprimir)).setVisibility(View.INVISIBLE);
                    ((ImageButton)view.findViewById(R.id.btnBT)).setEnabled(false);
                    ((Button)view.findViewById(R.id.btnfinal)).setVisibility(View.VISIBLE);
                }catch(Exception eo){
                }
            }
        });

        ((Button)view.findViewById(R.id.btnfinal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    closeBT();
                    getActivity().finish();
                }catch (Exception e){

                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setMessage("¿Seguro que desea eliminar este item?");
                alertDialog.setTitle("ELIMINAR ITEM");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DetalleVenta item = lista.get(position);
                        lista.remove(position);
                        calcularExentaRemove(item.getExenta());
                        calcular5Remove(item.getIva5());
                        calcular10Remove(item.getIva10());
                        calculartotalventaRemove(item.getTotal());
                        adaptadorProductos = new Adaptador_Productos_detalleventa(getContext(),lista);
                        adaptadorProductos.notifyDataSetChanged();
                        lv.setAdapter(adaptadorProductos);
                        view.setSelected(true);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

                return true;
            }
        });
    }

    public void cargarItems(){
        try{
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
    public void calcularExentaRemove( int remove){
        exentaT= exentaT-remove;
        ((TextView)vista.findViewById(R.id.idtotalexenta)).setText(String.valueOf(exentaT));
    }
    public void calcular5(){
        iva5T=iva5T+iva5;
        ((TextView)vista.findViewById(R.id.idtotal5)).setText(String.valueOf(iva5T));
    }
    public void calcular5Remove( int remove){
        iva5T=iva5T-remove;
        ((TextView)vista.findViewById(R.id.idtotal5)).setText(String.valueOf(iva5T));
    }
    public void calcular10(){
        iva10T=iva10T+iva10;
        ((TextView)vista.findViewById(R.id.idtotal10)).setText(String.valueOf(iva10T));
    }
    public void calcular10Remove(int remove){
        iva10T=iva10T-remove;
        ((TextView)vista.findViewById(R.id.idtotal10)).setText(String.valueOf(iva10T));
    }
    public void calculartotalventa(){
        totalfinal+=total;
        ((TextView)vista.findViewById(R.id.idtotalfinal)).setText(String.valueOf(totalfinal));
    }
    public void calculartotalventaRemove( int remove){
        totalfinal-=remove;
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
            db.openWritable();
            long insertarventa = db.insertarventa(idventas,nrofactura,condicion,fecha,
                    Integer.parseInt(((TextView)vista.findViewById(R.id.idtotalfinal)).getText().toString()),
                    Integer.parseInt(((TextView)vista.findViewById(R.id.idtotalexenta)).getText().toString())
                    ,Integer.parseInt(((TextView)vista.findViewById(R.id.idtotal5)).getText().toString()),
                    Integer.parseInt(((TextView)vista.findViewById(R.id.idtotal10)).getText().toString()),
                    idcliente,idusuario,idtimbrado,idemision);
            if(insertarventa>0){
                Toast.makeText(getContext(),"Venta registrada!",Toast.LENGTH_SHORT).show();
                GuardarDetalleVenta();
            }else{
                Toast.makeText(getContext(),"No se pudo registra la venta",Toast.LENGTH_SHORT).show();
            }
        db.close();
    }

    public void GuardarDetalleVenta(){
        Access_Venta db = Access_Venta.getInstance(getContext());
        db.openWritable();
            for(int i=1; i<=lista.size();i++){
                int position = (i-1);
                DetalleVenta item = lista.get(position);
                try{
                    long insertarDetalle = db.insertarDetalle(idventas,item.getIdproducto(),item.getCantidad(),
                            Integer.parseInt(item.getPrecio()),item.getTotal(),Integer.parseInt(item.getIvadescripcion()),item.getUm());
                    Log.i("detalle: ", String.valueOf(insertarDetalle));
                }catch(Exception e){
                    Toast.makeText(getContext(), "FATAL: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        ((Button)vista.findViewById(R.id.btn_idFVenta)).setVisibility(View.INVISIBLE);
        ((ImageButton) vista.findViewById(R.id.btnBT)).setVisibility(View.VISIBLE);
        db.close();
    }

    public void obtenerTimbrado(){
        Access_Timbrado tim = new Access_Timbrado(getContext());
        Cursor e= tim.getTimbradoActivo();
        if(e.moveToNext()){
            timbActual=e.getString(1);
            fdesde = e.getString(2);
            fhasta= e.getString(3);
        }else{
            timbActual="0";
            fdesde = "0";
            fhasta= "0";
        }
    }

    public void obtenerEmpresa(){
        Access_Empresa emp = new Access_Empresa(getContext());
        Cursor e = emp.getEmpresas();
        if(e.moveToNext()){
            empRZ = e.getString(1);
            empRUC = e.getString(2);
            empDir = e.getString(3);
            empTel = e.getString(4);
            empCiu = e.getString(6);
        }else{
            empRZ = "sin especificar";
            empRUC = "sin especificar";
            empDir = "sin especificar";
            empTel = "sin especificar";
            empCiu = "sin especificar";
        }

    }

private void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null) {
                myLabel.setText("No hay adaptador bluetooth disponible.");
            }
            if(mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableBluetooth);
                startActivityForResult(enableBluetooth, 0);
            }
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if(pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equals("RG-MDP58B")) {
                            mmDevice = device;
                            break;
                        }
                    }
                }
                myLabel.setText("Se encontró un dispositivo Bluetooth.");

        }catch(Exception e){/*Toast.makeText(getContext(),"findBT: "+e.getMessage(),Toast.LENGTH_LONG).show();//e.printStackTrace();*/}
    }

    private void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
            myLabel.setText("Bluetooth abierto, listo para imprimir.");
            ((Button)vista.findViewById(R.id.btnImprimir)).setVisibility(View.VISIBLE);
            ((ImageButton)vista.findViewById(R.id.btnBT)).setImageResource(R.drawable.ic_bluetooth_connected);
        } catch (Exception e) {/*Toast.makeText(getContext(),"openBT: "+e.getMessage(),Toast.LENGTH_LONG).show();//e.printStackTrace();*/}
    }
    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    private void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            Toast.makeText(getContext(),"beginListenForData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    private void sendData() throws IOException {
        try {
            if (mmSocket != null) {
                try {
                   String msg = "\n";
                    msg += "\n";
                    msg += "     "+empRZ+"\n";
                    msg += "        RUC:"+empRUC+"\n";
                    msg += "       Cel:"+empTel+"\n";
                    msg += "      "+empDir+" - PY"+"\n";
                    msg += "------------------------------\n";
                    msg += "     Timbrado N: "+timbActual+"\n";
                    msg += "  Inic. Vigencia: "+fdesde+"\n";
                    msg += "   Fin Vigencia: "+fhasta+"\n";
                    msg += "          IVA INCLUIDO      \n";
                    msg += "------------------------------\n";
                    msg += "Factura Nro: "+nestablecimiento+"-"+nemision+"-"+nrofactura+"\n";
                    msg += "Fecha/Hora: "+fecha+"\n";
                    msg += "Condicion: "+condicion+"\n";
                    msg += "Vendedor:"+vendedor+"\n";
                    msg += "------------------------------\n";
                    msg += "CLIENTE: "+vcliente+"\n";
                    msg += "RUC/CI: "+vruccliente+"\n";
                    msg += "------------------------------\n";
                    msg += String.format("%1$1s %2$5s %3$1s %4$6s %5$7s", "IVA", "CANT","", "PRECIO","   SUBTOTAL" );
                    msg += "\n";
                    msg += "------------------------------\n";
                    for(int i=1; i<=lista.size();i++) {
                        int position = (i - 1);
                        DetalleVenta item = lista.get(position);
                        msg += String.format("%1$1s" , item.producto+"\n");
                        msg += String.format("%1$1s %2$6s %3$7s %4$8s" , item.ivadescripcion+"%",item.cantidad+" "+item.um, item.precio, item.total)+ "\n";
                    }
                    msg += "------------------------------\n";
                    msg += "TOTAL                  "+((TextView)vista.findViewById(R.id.idtotalfinal)).getText().toString()+"\n";
                    msg += "------------------------------\n";
                    msg += "********** TOTALES **********"+"\n";
                    msg += "EXENTAS   -->          "+((TextView)vista.findViewById(R.id.idtotalexenta)).getText().toString()+"\n";
                    msg += "GRAV. 5%  -->          "+((TextView)vista.findViewById(R.id.idtotal5)).getText().toString()+"\n";
                    msg += "GRAV. 10% -->          "+((TextView)vista.findViewById(R.id.idtotal10)).getText().toString()+"\n\n";
                    msg += "**** LIQUIDACION DEL IVA  ****"+"\n";
                    msg += "IVA 5%    -->          "+Math.round(Double.parseDouble(((TextView)vista.findViewById(R.id.idtotal5)).getText().toString())/21)+"\n";
                    msg += "IVA 10%   -->          "+Math.round(Double.parseDouble(((TextView)vista.findViewById(R.id.idtotal10)).getText().toString())/11)+"\n";
                    msg += "------------------------------\n";
                    long totaliva=(Math.round(Double.parseDouble(((TextView)vista.findViewById(R.id.idtotal5)).getText().toString())/21)+Math.round(Double.parseDouble(((TextView)vista.findViewById(R.id.idtotal10)).getText().toString())/11));
                    msg += "TOTAL IVA              "+totaliva+"\n";
                    msg += "------------------------------\n";
                    msg += "Original: Cliente\n";
                    msg += "Duplicado: Archivo Tributario\n";
                    msg += "\n  GRACIAS POR SU PREFERENCIA\n";
                    msg += "                              \n\n";

                    myLabel.setText("Espere que finalice la impresion para cerrar.");
                    // Para que acepte caracteres espciales
                    mmOutputStream.write(0x1C); mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B); mmOutputStream.write(0x74); mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252

                    mmOutputStream.write(getByteString(msg));

                    mmOutputStream.write("\n\n".getBytes());

                } catch (IOException e) {
                    Log.e("MENSAJE: ", "Error al escribir en el socket");

                    Toast.makeText(getContext(), "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //closeBT();
            } else {
                Log.e("MENSAJE: ", "Socket nulo");

                myLabel.setText("Impresora no conectada");
            }

        } catch (Exception e) {
            Toast.makeText(getContext(),"sendData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // close the connection to bluetooth printer.
    private void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth cerrado");

        } catch (Exception e) {/*Toast.makeText(getContext(),"closeBT: "+e.getMessage(),Toast.LENGTH_LONG).show();//e.printStackTrace();*/}
    }
    public static byte[] getByteString(String str/*, int bold, int font, int widthsize, int heigthsize*/) {
        if (str.length() == 0)
            return null;
        byte[] strData = null;
        try {
            strData = str.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        byte[] command = new byte[strData.length + 9];
        //byte[] intToWidth = {0x00, 0x10, 0x20, 0x30};//
        //byte[] intToHeight = {0x00, 0x01, 0x02, 0x03};//
        command[0] = 27;// caracter ESC para darle comandos a la impresora
        command[1] = 69;
        // command[2] = ((byte) bold);
        command[3] = 27;
        command[4] = 77;
        //command[5] = ((byte) font);
        command[6] = 29;
        command[7] = 33;
        //command[8] = (byte) (intToWidth[widthsize] + intToHeight[heigthsize]);
        System.arraycopy(strData, 0, command, 9, strData.length);
        return command;
    }

    private void AlertaRegistrar(){
        if(lista.size()==0){
            Toast.makeText(getContext(), "Lista vacía", Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setMessage("¿Seguro que desea REGISTRAR la venta?");
            alertDialog.setTitle("REGISTRAR");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Sí, registrar", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    Guardar();
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }
}