package com.uade.TPO_Ecommerce_Grupo6.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoDetalleResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoResumenResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.ProductoService;

/**
 * Módulo 4 — Catálogo (solo lectura). El Módulo 5 va a agregar acá mismo los
 * métodos POST/PUT/DELETE de gestión de productos.
 */

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Home: GET /api/productos (alfabético) o GET /api/productos?categoriaId=3
    @GetMapping
    public ResponseEntity<List<ProductoResumenResponse>> listar(
            @RequestParam(required = false) Long categoriaId) {

        return ResponseEntity.ok(productoService.listarTodos(categoriaId));
    }

    // Detalle: imagen ampliada + descripción completa.
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDetalleResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(productoService.buscarPorId(id));
    }
}

