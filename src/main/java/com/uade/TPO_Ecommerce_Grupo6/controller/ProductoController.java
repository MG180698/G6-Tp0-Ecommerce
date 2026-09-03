package com.uade.TPO_Ecommerce_Grupo6.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoDetalleResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoResumenResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.ProductoService;

import jakarta.validation.Valid;


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

    @PostMapping
    public ResponseEntity<ProductoDetalleResponse> crear(
            @Valid @RequestBody ProductoRequest request) {

        ProductoDetalleResponse productoCreado = productoService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoCreado);
    }
}
