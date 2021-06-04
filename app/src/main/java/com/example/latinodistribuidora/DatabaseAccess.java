package com.example.latinodistribuidora;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.latinodistribuidora.Modelos.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    public DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    public void insertarUsuario(String nombre, String ci, String direccion, String celular, String usuario, String contrasena){
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("ci", ci);
        values.put("direccion", direccion);
        values.put("telefono", celular);
        values.put("usuario", usuario);
        values.put("contrasena", contrasena);
        values.put("estado", "S");
        db.insert("usuario",null,values);
    }

    public ArrayList getUsuarios(){
        ArrayList<String> lista = new ArrayList<>();
        this.open();
        Cursor registros = db.rawQuery("Select * from usuario", null);
        if (registros.moveToFirst()){
            do {
                lista.add(registros.getString(1));
                //lista.add(registros.getString(2));
                //lista.add(registros.getString(3));
                //lista.add(registros.getString(4));
                //lista.add(registros.getString(5));
            }while (registros.moveToNext());
        }
        return lista;
    }

    /*public List<Usuario> getUsuarios(){
        List<Usuario> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * from usuario", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Usuario usuario = new Usuario();
            usuario.setNombre(cursor.getString(1));
            usuario.setCi(cursor.getString(2));
            usuario.setDireccion(cursor.getString(3));
            usuario.setCelular(cursor.getString(4));
            usuario.setUsuario(cursor.getString(5));
            list.add(usuario);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }*/





    /*public String getAddress(String nombre){
        c = db.rawQuery( "select Address from Table1 where Name = '"+nombre+"'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()){
            String address = c.getString(0);
            buffer.append(""+address);
        }
        return buffer.toString();
    }*/
}
