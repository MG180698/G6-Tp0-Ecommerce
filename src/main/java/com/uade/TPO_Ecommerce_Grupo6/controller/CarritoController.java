package com.uade.TPO_Ecommerce_Grupo6.controller;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.ActualizarItemCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.AgregarItemCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CarritoDTO;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CrearCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(@Valid @RequestBody CrearCarritoRequest request) {
        CarritoDTO carrito = carritoService.crearCarrito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrito);
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(@Valid @RequestBody AgregarItemCarritoRequest request) {
        return ResponseEntity.ok(carritoService.agregarItem(request));
    }

    @PutMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(@PathVariable Long productoId, @Valid @RequestBody ActualizarItemCarritoRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(productoId, request));
    }

    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarItem(@PathVariable Long productoId, @RequestParam Long usuarioId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, productoId));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> vaciarCarrito(@RequestParam Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
