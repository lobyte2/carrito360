package com.botilleria.carrito.controller;

import com.botilleria.carrito.model.Carrito;
import com.botilleria.carrito.model.ItemCarrito;
import com.botilleria.carrito.service.CarritoService;
import com.botilleria.carrito.service.KafkaCarritoProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private static final Map<String, Integer> CUPONES_VALIDOS = Map.of(
            "PEDIDOS10", 10,
            "BIENVENIDO15", 15,
            "CLOUD20", 20,
            "ENVIOGRATIS", 5
    );

    @Autowired
    private CarritoService carritoService;

    @Autowired(required = false)
    private KafkaCarritoProducer kafkaCarritoProducer;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Carrito> verCarrito(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(carritoService.obtenerPorUsuario(idUsuario));
    }

    @PostMapping("/{idUsuario}/agregar")
    public ResponseEntity<Carrito> agregarAlCarrito(@PathVariable Long idUsuario, @RequestBody ItemCarrito articulo) {
        Carrito actualizado = carritoService.agregarArticulo(idUsuario, articulo);
        if (kafkaCarritoProducer != null) {
            kafkaCarritoProducer.publicarItemAgregado(idUsuario, articulo);
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{idUsuario}/limpiar")
    public ResponseEntity<Void> limpiar(@PathVariable Long idUsuario) {
        carritoService.limpiarCarrito(idUsuario);
        if (kafkaCarritoProducer != null) {
            kafkaCarritoProducer.publicarCarritoLimpiado(idUsuario);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{idUsuario}/cupon")
    public ResponseEntity<Map<String, Object>> aplicarCupon(
            @PathVariable Long idUsuario,
            @RequestBody Map<String, String> body) {
        String codigo = body.getOrDefault("codigo", "").trim().toUpperCase(Locale.ROOT);
        Integer descuento = CUPONES_VALIDOS.get(codigo);
        if (descuento == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valido", false,
                    "mensaje", "Cupon invalido. Prueba: PEDIDOS10, BIENVENIDO15, CLOUD20 o ENVIOGRATIS."
            ));
        }
        if (kafkaCarritoProducer != null) {
            kafkaCarritoProducer.publicarCuponAplicado(idUsuario, codigo, descuento);
        }
        return ResponseEntity.ok(Map.of(
                "valido", true,
                "codigo", codigo,
                "descuentoPorcentaje", descuento,
                "mensaje", "Cupon " + codigo + " aplicado (" + descuento + "% de descuento)."
        ));
    }
}
