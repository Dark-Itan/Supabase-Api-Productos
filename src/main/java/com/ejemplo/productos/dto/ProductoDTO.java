package com.ejemplo.productos.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoDTO(
        UUID id,
        String nombre,
        BigDecimal precio,
        Integer stock
) {
}