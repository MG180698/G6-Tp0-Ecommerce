package com.uade.TPO_Ecommerce_Grupo6.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoDetalleResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoResumenResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Módulo 4 — Catálogo (solo lectura). El Módulo 5 agrega acá mismo los
 * métodos POST/PUT/DELETE de gestión de productos.
 */
@Tag(name = "Productos", description = "Endpoints de catálogo y gestión de productos")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Home: GET /api/productos (alfabético) o GET /api/productos?categoriaId=3
    @Operation(summary = "Listar productos con orden alfabético y filtro opcional por categoría")
    @GetMapping
    public ResponseEntity<List<ProductoResumenResponse>> listar(
            @RequestParam(required = false) Long categoriaId) {

        return ResponseEntity.ok(productoService.listarTodos(categoriaId));
    }

    // Detalle: imagen ampliada + descripción completa.
    @Operation(summary = "Obtener el detalle de un producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDetalleResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @Operation(summary = "Crear una nueva publicación de producto")
    @PostMapping
    public ResponseEntity<ProductoDetalleResponse> crear(
            @Valid @RequestBody ProductoRequest request) {

        ProductoDetalleResponse productoCreado = productoService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoCreado);
    }

    @Operation(summary = "Actualizar una publicación de producto existente")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDetalleResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {

        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una publicación de producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        productoService.eliminar(id, usuarioId);

        return ResponseEntity.noContent().build();
    }
}