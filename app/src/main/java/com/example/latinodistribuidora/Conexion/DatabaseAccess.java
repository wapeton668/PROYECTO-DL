package com.example.latinodistribuidora.Conexion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    //CRUD TABLA USUARIO*********************************************************************************************

    public long insertarUsuario(String nombre, String ci, String direccion, String celular, String usuario, String contrasena){
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("ci", ci);
        values.put("direccion", direccion);
        values.put("telefono", celular);
        values.put("usuario", usuario);
        values.put("contrasena", contrasena);
        values.put("estado", "S");
        long insertado = db.insert("usuario",null,values);

        return insertado;
    }
    public long eliminarUsuario(int ID){
        this.openWritable();
        long u=db.delete("usuario", "idusuario="+ID,null);
        this.close();
        return u;
    }

    public Cursor getUsuarios(){
        //ArrayList<Usuarios> lista = new ArrayList<>();
        this.openWritable();
        Cursor registros = db.rawQuery("Select * from usuario where estado='S'", null);
        return registros;
    }

    public Cursor getUsuario_a_modificar(int usuarioEditar) {
        this.openReadable();
        Cursor registro = db.rawQuery("Select * from usuario where idusuario="+usuarioEditar, null);
        return  registro;
    }

    public long ActualizarUsuario(ContentValues values, int usuarioEditar) {
        this.openWritable();
        long accion = db.update("usuario",values, "idusuario="+usuarioEditar,null);
        return accion;
    }
}
