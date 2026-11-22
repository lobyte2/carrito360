package com.botilleria.carrito.service;

import com.botilleria.carrito.model.ItemCarrito;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KafkaCarritoProducer {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @Value("${app.kafka.topic-carrito:carrito.eventos}")
    private String topicCarrito;

    public void publicarItemAgregado(Long idUsuario, ItemCarrito item) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("tipoEvento", "CARRITO_AGREGADO");
        evento.put("origen", "ms-carrito");
        evento.put("usuarioId", String.valueOf(idUsuario));
        evento.put("productoId", item.getIdProducto());
        evento.put("productoNombre", item.getNombreProducto() != null ? item.getNombreProducto() : "Producto #" + item.getIdProducto());
        evento.put("cantidad", item.getCantidad());
        if (item.getPrecio() != null) {
            evento.put("precio", item.getPrecio());
        }
        evento.put("timestamp", Instant.now().toString());
        enviar(String.valueOf(idUsuario), evento);
    }

    public void publicarCarritoLimpiado(Long idUsuario) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("tipoEvento", "CARRITO_LIMPIADO");
        evento.put("origen", "ms-carrito");
        evento.put("usuarioId", String.valueOf(idUsuario));
        evento.put("timestamp", Instant.now().toString());
        enviar(String.valueOf(idUsuario), evento);
    }

    public void publicarCuponAplicado(Long idUsuario, String codigoCupon, int porcentajeDescuento) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("tipoEvento", "CUPON_APLICADO");
        evento.put("origen", "ms-carrito");
        evento.put("usuarioId", String.valueOf(idUsuario));
        evento.put("cupon", codigoCupon);
        evento.put("descuentoPorcentaje", porcentajeDescuento);
        evento.put("timestamp", Instant.now().toString());
        enviar(String.valueOf(idUsuario), evento);
    }

    private void enviar(String key, Map<String, Object> evento) {
        if (!kafkaEnabled || kafkaTemplate == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send(topicCarrito, key, json);
        } catch (Exception e) {
            System.err.println("[ms-carrito] Aviso Kafka (no bloqueante): " + e.getMessage());
        }
    }
}
