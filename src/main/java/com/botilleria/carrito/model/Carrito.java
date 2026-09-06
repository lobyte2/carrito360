package com.botilleria.carrito.model;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private Long idUsuario;
    private List<ItemCarrito> articulos;

    public Carrito() {
        this.articulos = new ArrayList<>();
    }

    public Carrito(Long idUsuario) {
        this.idUsuario = idUsuario;
        this.articulos = new ArrayList<>();
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public List<ItemCarrito> getArticulos() { return articulos; }
    public void setArticulos(List<ItemCarrito> articulos) { this.articulos = articulos; }
}