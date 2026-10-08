package com.ejemplo.productos.service.impl;

import com.ejemplo.productos.dto.ActualizarProductoRequest;
import com.ejemplo.productos.dto.CrearProductoRequest;
import com.ejemplo.productos.dto.ProductoDTO;
import com.ejemplo.productos.exception.ProductoNotFoundException;
import com.ejemplo.productos.exception.SupabaseException;
import com.ejemplo.productos.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private static final String TABLA = "/productos";
    private static final String SELECT_ALL = "?select=*";

    private final RestClient supabaseRestClient;

    public ProductoServiceImpl(RestClient supabaseRestClient) {
        this.supabaseRestClient = supabaseRestClient;
    }

    @Override
    public List<ProductoDTO> listarTodos() {
        logger.info("Listando todos los productos");

        try {
            List<ProductoDTO> productos = supabaseRestClient.get()
                    .uri(TABLA + SELECT_ALL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

            logger.info("Productos obtenidos: {}", productos != null ? productos.size() : 0);
            return productos != null ? productos : List.of();

        } catch (RestClientException ex) {
            logger.error("Error al listar productos: {}", ex.getMessage());
            throw new SupabaseException("No se pudo obtener la lista de productos", ex);
        }
    }

    @Override
    public ProductoDTO obtenerPorId(UUID id) {
        logger.info("Buscando producto con ID: {}", id);

        try {
            List<ProductoDTO> resultado = supabaseRestClient.get()
                    .uri(TABLA + "?id=eq." + id + "&select=*")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

            if (resultado == null || resultado.isEmpty()) {
                logger.warn("Producto no encontrado con ID: {}", id);
                throw new ProductoNotFoundException("Producto no encontrado con ID: " + id);
            }

            logger.info("Producto encontrado con ID: {}", id);
            return resultado.get(0);

        } catch (RestClientException ex) {
            logger.error("Error al buscar producto con ID {}: {}", id, ex.getMessage());
            throw new SupabaseException("No se pudo obtener el producto", ex);
        }
    }

    @Override
    public ProductoDTO crear(CrearProductoRequest request) {
        logger.info("Creando producto: {}", request.nombre());

        try {
            List<ProductoDTO> resultado = supabaseRestClient.post()
                    .uri(TABLA)
                    .header("Prefer", "return=representation")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

            if (resultado == null || resultado.isEmpty()) {
                logger.error("No se pudo crear el producto");
                throw new SupabaseException("No se pudo crear el producto");
            }

            ProductoDTO productoCreado = resultado.get(0);
            logger.info("Producto creado con ID: {}", productoCreado.id());
            return productoCreado;

        } catch (RestClientException ex) {
            logger.error("Error al crear producto: {}", ex.getMessage());
            throw new SupabaseException("No se pudo crear el producto", ex);
        }
    }

    @Override
    public ProductoDTO actualizar(UUID id, ActualizarProductoRequest request) {
        logger.info("Actualizando producto con ID: {}", id);

        obtenerPorId(id);

        try {
            List<ProductoDTO> resultado = supabaseRestClient.patch()  // ← CAMBIO: patch() → put()
                    .uri(TABLA + "?id=eq." + id)
                    .header("Prefer", "return=representation")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductoDTO>>() {});

            if (resultado == null || resultado.isEmpty()) {
                logger.error("No se pudo actualizar el producto con ID: {}", id);
                throw new SupabaseException("No se pudo actualizar el producto");
            }

            ProductoDTO productoActualizado = resultado.get(0);
            logger.info("Producto actualizado con ID: {}", id);
            return productoActualizado;

        } catch (RestClientException ex) {
            logger.error("Error al actualizar producto con ID {}: {}", id, ex.getMessage());
            throw new SupabaseException("No se pudo actualizar el producto", ex);
        }
    }

    @Override
    public void eliminar(UUID id) {
        logger.info("Eliminando producto con ID: {}", id);

        obtenerPorId(id);

        try {
            supabaseRestClient.delete()
                    .uri(TABLA + "?id=eq." + id)
                    .retrieve()
                    .toBodilessEntity();

            logger.info("Producto eliminado con ID: {}", id);

        } catch (RestClientException ex) {
            logger.error("Error al eliminar producto con ID {}: {}", id, ex.getMessage());
            throw new SupabaseException("No se pudo eliminar el producto", ex);
        }
    }
}