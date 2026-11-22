package com.botilleria.carrito.service;

import com.botilleria.carrito.dto.CarritoEventoDto;
import com.botilleria.carrito.model.ItemCarrito;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
        CarritoEventoDto evento = new CarritoEventoDto();
        evento.setTipoEvento("CARRITO_AGREGADO");
        evento.setOrigen("ms-carrito");
        evento.setUsuarioId(String.valueOf(idUsuario));
        evento.setProductoId(item.getIdProducto());
        evento.setProductoNombre(item.getNombreProducto() != null ? item.getNombreProducto() : "Producto #" + item.getIdProducto());
        evento.setCantidad(item.getCantidad());
        evento.setPrecio(item.getPrecio());
        evento.setTimestamp(Instant.now().toString());
        enviar(String.valueOf(idUsuario), evento);
    }

    public void publicarCarritoLimpiado(Long idUsuario) {
        CarritoEventoDto evento = new CarritoEventoDto();
        evento.setTipoEvento("CARRITO_LIMPIADO");
        evento.setOrigen("ms-carrito");
        evento.setUsuarioId(String.valueOf(idUsuario));
        evento.setTimestamp(Instant.now().toString());
        enviar(String.valueOf(idUsuario), evento);
    }

    public void publicarCuponAplicado(Long idUsuario, String codigoCupon, int porcentajeDescuento) {
        CarritoEventoDto evento = new CarritoEventoDto();
        evento.setTipoEvento("CUPON_APLICADO");
        evento.setOrigen("ms-carrito");
        evento.setUsuarioId(String.valueOf(idUsuario));
        evento.setCupon(codigoCupon);
        evento.setDescuentoPorcentaje(porcentajeDescuento);
        evento.setTimestamp(Instant.now().toString());
        enviar(String.valueOf(idUsuario), evento);
    }

    private void enviar(String key, CarritoEventoDto evento) {
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
