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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<CarritoDTO> obtenerCarrito(Authentication authentication) {
        String username = obtenerUsername(authentication);
        return ResponseEntity.ok(carritoService.obtenerCarrito(username));
    }

    @Operation(summary = "Agregar un producto al carrito (crea el carrito automáticamente si no existe)")
    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(@Valid @RequestBody AgregarItemCarritoRequest request, Authentication authentication) {
        String username = obtenerUsername(authentication);
        return ResponseEntity.ok(carritoService.agregarItem(username, request));
    }

    @Operation(summary = "Actualizar la cantidad de un producto en el carrito")
    @PutMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(@PathVariable Long productoId, @Valid @RequestBody ActualizarItemCarritoRequest request, Authentication authentication) {
        String username = obtenerUsername(authentication);
        return ResponseEntity.ok(carritoService.actualizarCantidad(username, productoId, request));
    }

    @Operation(summary = "Eliminar un producto del carrito")
    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarItem(@PathVariable Long productoId, Authentication authentication) {
        String username = obtenerUsername(authentication);
        return ResponseEntity.ok(carritoService.eliminarItem(username, productoId));
    }

    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(Authentication authentication) {
        String username = obtenerUsername(authentication);
        CarritoDTO carrito = carritoService.crearCarrito(username);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carrito);
    }

    @Operation(summary = "Vaciar todos los items del carrito")
    @DeleteMapping("/items")
    public ResponseEntity<Void> vaciarCarrito(Authentication authentication) {
        String username = obtenerUsername(authentication);
        carritoService.vaciarCarrito(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Realizar el checkout del carrito (Módulo 8)")
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(Authentication authentication) {
        String username = obtenerUsername(authentication);
        CheckoutResponse response = carritoService.checkout(username);
        return ResponseEntity.ok(response);
    }


    private String obtenerUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario debe iniciar sesión");
        }
        return authentication.getName();
    }
}
