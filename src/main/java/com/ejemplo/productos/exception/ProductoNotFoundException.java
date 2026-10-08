package com.ejemplo.productos.exception;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(String message) {
        super(message);
    }
}