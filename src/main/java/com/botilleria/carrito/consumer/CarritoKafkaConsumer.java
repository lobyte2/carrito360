package com.botilleria.carrito.consumer;

import com.botilleria.carrito.dto.ProductoEventoRecibidoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka en ms-carrito: escucha eventos de navegacion de productos.eventos
 * para correlacionar interes de compra con el carrito activo.
 */
@Component
public class CarritoKafkaConsumer {

    private final ObjectMapper objectMapper;

    public CarritoKafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topic-productos:productos.eventos}",
            groupId = "${spring.kafka.consumer.group-id:ms-carrito-group}",
            autoStartup = "${app.kafka.enabled:true}"
    )
    public void consumirEventoProducto(String payload) {
        ProductoEventoRecibidoDto evento;
        try {
            evento = objectMapper.readValue(payload, ProductoEventoRecibidoDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload no es JSON valido en productos.eventos", e);
        }
        System.out.println("[MS-CARRITO][KAFKA] Interes de usuario detectado: " + evento.getTipoEvento()
                + " | usuario=" + evento.getUsuarioId()
                + " | producto=" + evento.getProductoNombre());
    }
}
