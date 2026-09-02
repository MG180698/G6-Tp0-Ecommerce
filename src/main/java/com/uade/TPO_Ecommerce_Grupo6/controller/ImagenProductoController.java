package com.uade.TPO_Ecommerce_Grupo6.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.imagen.ImagenProductoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.imagen.ImagenProductoResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.ImagenProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ImagenProductoController {

    private final ImagenProductoService imagenProductoService;

    public ImagenProductoController(ImagenProductoService imagenProductoService) {
        this.imagenProductoService = imagenProductoService;
    }

    // POST /api/productos/{id}/imagenes -> agrega una foto a un producto existente
    @PostMapping("/{id}/imagenes")
    public ResponseEntity<ImagenProductoResponse> agregarImagen(
            @PathVariable("id") Long productoId,
            @Valid @RequestBody ImagenProductoRequest request) {

        ImagenProductoResponse response = imagenProductoService.agregarImagen(productoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}