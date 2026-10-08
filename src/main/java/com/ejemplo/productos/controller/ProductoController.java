package com.ejemplo.productos.controller;

import com.ejemplo.productos.dto.ActualizarProductoRequest;
import com.ejemplo.productos.dto.CrearProductoRequest;
import com.ejemplo.productos.dto.ProductoDTO;
import com.ejemplo.productos.service.ProductoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        logger.info("Peticion GET recibida en /api/v1/productos");

        List<ProductoDTO> productos = productoService.listarTodos();

        logger.info("Retornando {} productos", productos.size());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable UUID id) {
        logger.info("Peticion GET recibida en /api/v1/productos/{}", id);

        ProductoDTO producto = productoService.obtenerPorId(id);

        logger.info("Producto encontrado con ID: {}", id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody CrearProductoRequest request) {
        logger.info("Peticion POST recibida en /api/v1/productos");

        ProductoDTO productoCreado = productoService.crear(request);

        logger.info("Producto creado exitosamente con ID: {}", productoCreado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarProductoRequest request) {

        logger.info("Peticion PUT recibida en /api/v1/productos/{}", id);

        ProductoDTO productoActualizado = productoService.actualizar(id, request);

        logger.info("Producto actualizado exitosamente con ID: {}", id);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        logger.info("Peticion DELETE recibida en /api/v1/productos/{}", id);

        productoService.eliminar(id);

        logger.info("Producto eliminado exitosamente con ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}