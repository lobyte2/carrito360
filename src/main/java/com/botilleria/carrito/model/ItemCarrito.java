package com.botilleria.carrito.model;

public class ItemCarrito {
    private Long idProducto;
    private Integer cantidad;

    public ItemCarrito() {}

    public ItemCarrito(Long idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}