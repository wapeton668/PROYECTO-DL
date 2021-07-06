package com.example.latinodistribuidora.CRUD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.latinodistribuidora.Conexion.DatabaseOpenHelper;

public class Access_Venta {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static Access_Venta instance;
    Cursor registros = null;
    Cursor Filtrar=null;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    public Access_Venta(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static Access_Venta getInstance(Context context) {
        if (instance == null) {
            instance = new Access_Venta(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void openWritable() {
        this.db = openHelper.getWritableDatabase();
    }

    public void openReadable() {
        this.db = openHelper.getReadableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }



    public Cursor getv_venta(String texto){
        this.openReadable();
        registros = db.rawQuery("Select * from v_ventas where fecha like '%"+texto+"%'", null);
        return registros;
    }

    public Cursor getFiltrarv_venta(String fechaactual,String texto){
        this.openReadable();
        Filtrar = db.rawQuery("select * from v_ventas where fecha like '%"+fechaactual+"%' and " +
                "factura like '%"+texto+"%' or razon_social like '%"+texto+"%' or ruc like'%"+texto+"%' order by id asc", null);
        return  Filtrar;
    }

    public Cursor getVenta(){
        this.openWritable();
        registros = db.rawQuery("Select * from ventas where estado='S'", null);
        return registros;
    }

    public Cursor getCodigo() {
        this.openWritable();
        registros = db.rawQuery("select MAX(idventas) from ventas",null);
        return registros;
    }

    /*public Cursor getFiltrarClientes(String texto){
        this.openReadable();
        Filtrar = db.rawQuery("select * from v_clientes where razon_social like '%"+texto+"%' and estado='S'", null);
        return  Filtrar;
    }*/

    public long insertarventa(int idventas,String nrofactura, String condicion, String fecha, int total, int exenta, int iva5, int iva10, int idcliente,
                              int idusuario, int idtimbrado, int idemision){
        ContentValues values = new ContentValues();
        values.put("idventas", idventas);
        values.put("nrofactura", nrofactura);
        values.put("condicion", condicion);
        values.put("fecha", fecha);
        values.put("total", total);
        values.put("exenta", exenta);
        values.put("iva5", iva5);
        values.put("iva10", iva10);
        values.put("estado", "S");
        values.put("cliente_idcliente", idcliente);
        values.put("usuario_idusuario", idusuario);
        values.put("idtimbrado", idtimbrado);
        values.put("idemision", idemision);

        long insertado = db.insert("ventas",null,values);

        return insertado;
    }

    public long insertarDetalle(int idventa, int idproducto, String cantidad, int precio,int total, int impuesto){
        ContentValues values = new ContentValues();
        values.put("venta_idventa", idventa);
        values.put("productos_idproductos", idproducto);
        values.put("cantidad", cantidad);
        values.put("precio", precio);
        values.put("total", total);
        values.put("impuesto_aplicado", impuesto);
        long insertado = db.insert("detalleventa",null,values);
        return insertado;
    }

   /* public boolean insertarDetalle(int idventa, int idproducto, String cantidad, int precio, int impuesto){

        boolean wasSuccess = true;
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("venta_idventa", idventa);
            values.put("productos_idproductos", idventa);
            values.put("cantidad", idventa);
            values.put("precio", idventa);
            values.put("impuesto_aplicado", idventa);
            long insertado = db.insert("detalleventa",null,values);
            if (insertado==-1){
                wasSuccess=false;
            }else {
                db.setTransactionSuccessful();
            }
        }catch (Exception e){
            Log.i("jd",e.getMessage());
            //
            //db.close();
        }
        db.endTransaction();
        db.close();
        return wasSuccess;

    }*/

    /*public Cursor getCliente_a_modificar(int clienteEditar) {
        this.openReadable();
        registros = db.rawQuery("Select * from v_clientes where idcliente="+clienteEditar, null);
        return  registros;
    }

    public long ActualizarCliente(ContentValues values, int clienteEditar) {
        this.openWritable();
        long accion = db.update("clientes",values, "idcliente="+clienteEditar,null);
        return accion;
    }
    public long EliminarCliente(int ID) {
        ContentValues values = new ContentValues();
        values.put("estado", "N");
        this.openWritable();
        long accion = db.update("clientes",values, "idcliente="+ID,null);
        return accion;
    }*/

}
