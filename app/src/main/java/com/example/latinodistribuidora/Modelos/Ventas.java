package com.example.latinodistribuidora.Modelos;

import java.io.Serializable;

public class Ventas implements Serializable {

    private int id,total,exenta,iva5,iva10;
    private String est, emision, factura, timbra, condicion, fecha,cliente,ruc,nombre,estado;

    public Ventas(int id, int total, int exenta, int iva5, int iva10, String est, String emision, String factura,
                  String timbra, String condicion, String fecha, String cliente, String ruc, String nombre, String estado) {
        this.id = id;
        this.total = total;
        this.exenta = exenta;
        this.iva5 = iva5;
        this.iva10 = iva10;
        this.est = est;
        this.emision = emision;
        this.factura = factura;
        this.timbra = timbra;
        this.condicion = condicion;
        this.fecha = fecha;
        this.cliente = cliente;
        this.ruc = ruc;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getExenta() {
        return exenta;
    }

    public void setExenta(int exenta) {
        this.exenta = exenta;
    }

    public int getIva5() {
        return iva5;
    }

    public void setIva5(int iva5) {
        this.iva5 = iva5;
    }

    public int getIva10() {
        return iva10;
    }

    public void setIva10(int iva10) {
        this.iva10 = iva10;
    }

    public String getEst() {
        return est;
    }

    public void setEst(String est) {
        this.est = est;
    }

    public String getEmision() {
        return emision;
    }

    public void setEmision(String emision) {
        this.emision = emision;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getTimbra() {
        return timbra;
    }

    public void setTimbra(String timbra) {
        this.timbra = timbra;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
