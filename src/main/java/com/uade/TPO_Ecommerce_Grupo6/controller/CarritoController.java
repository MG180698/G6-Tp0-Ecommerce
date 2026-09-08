package com.uade.TPO_Ecommerce_Grupo6.controller;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.ActualizarItemCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.AgregarItemCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CarritoDTO;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CheckoutResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carrito", description = "Endpoints para la gestión del carrito de compras y checkout")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(summary = "Obtener el carrito de un usuario")
    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @Operation(summary = "Agregar un producto al carrito (crea el carrito automáticamente si no existe)")
    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(@Valid @RequestBody AgregarItemCarritoRequest request) {
        return ResponseEntity.ok(carritoService.agregarItem(request));
    }

    @Operation(summary = "Actualizar la cantidad de un producto en el carrito")
    @PutMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(@PathVariable Long productoId, @Valid @RequestBody ActualizarItemCarritoRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(productoId, request));
    }

    @Operation(summary = "Eliminar un producto del carrito")
    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarItem(@PathVariable Long productoId, @RequestParam Long usuarioId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, productoId));
    }

    @Operation(summary = "Vaciar todos los items del carrito")
    @DeleteMapping("/items")
    public ResponseEntity<Void> vaciarCarrito(@RequestParam Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Realizar el checkout del carrito (Módulo 8)")
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestParam Long usuarioId) {
        CheckoutResponse response = carritoService.checkout(usuarioId);
        return ResponseEntity.ok(response);
    }
}
