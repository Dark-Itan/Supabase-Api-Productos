package com.ejemplo.productos.service;

import com.ejemplo.productos.dto.ActualizarProductoRequest;
import com.ejemplo.productos.dto.CrearProductoRequest;
import com.ejemplo.productos.dto.ProductoDTO;

import java.util.List;
import java.util.UUID;

public interface ProductoService {

    List<ProductoDTO> listarTodos();

    ProductoDTO obtenerPorId(UUID id);

    ProductoDTO crear(CrearProductoRequest request);

    ProductoDTO actualizar(UUID id, ActualizarProductoRequest request);

    void eliminar(UUID id);
}