package com.botilleria.carrito.service;

import com.botilleria.carrito.model.Carrito;
import com.botilleria.carrito.model.ItemCarrito;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {
    private List<Carrito> memoriaCarritos = new ArrayList<>();

    public Carrito obtenerPorUsuario(Long idUsuario) {
        return memoriaCarritos.stream()
                .filter(c -> c.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito(idUsuario);
                    memoriaCarritos.add(nuevo);
                    return nuevo;
                });
    }

    public Carrito agregarArticulo(Long idUsuario, ItemCarrito articulo) {
        Carrito carrito = obtenerPorUsuario(idUsuario);
        carrito.getArticulos().add(articulo);
        return carrito;
    }

    public void limpiarCarrito(Long idUsuario) {
        Carrito carrito = obtenerPorUsuario(idUsuario);
        carrito.getArticulos().clear();
    }
}