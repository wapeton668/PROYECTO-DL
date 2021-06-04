package com.example.latinodistribuidora.Modelos;

import java.io.Serializable;

public class Usuario  implements Serializable {
    private String nombre;
    private String ci;
    private String direccion;
    private String celular;
    private String usuario;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {return nombre +" "+ ci ;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o==null||getClass()!=o.getClass()) return  false;

        Usuario usuario = (Usuario) o;

        if(!usuario.equals(usuario.usuario)) return  false;

        return  true;
    }
    @Override
    public int hashCode() { return usuario.hashCode(); }




}
