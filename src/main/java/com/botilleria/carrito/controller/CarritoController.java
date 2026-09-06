package com.botilleria.carrito.controller;

import com.botilleria.carrito.model.Carrito;
import com.botilleria.carrito.model.ItemCarrito;
import com.botilleria.carrito.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Carrito> verCarrito(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(carritoService.obtenerPorUsuario(idUsuario));
    }

    @PostMapping("/{idUsuario}/agregar")
    public ResponseEntity<Carrito> agregarAlCarrito(@PathVariable Long idUsuario, @RequestBody ItemCarrito articulo) {
        return ResponseEntity.ok(carritoService.agregarArticulo(idUsuario, articulo));
    }

    @DeleteMapping("/{idUsuario}/limpiar")
    public ResponseEntity<Void> limpiar(@PathVariable Long idUsuario) {
        carritoService.limpiarCarrito(idUsuario);
        return ResponseEntity.ok().build();
    }
}