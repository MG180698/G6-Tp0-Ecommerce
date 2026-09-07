package com.uade.TPO_Ecommerce_Grupo6.service;

import com.uade.TPO_Ecommerce_Grupo6.exception.*;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.ActualizarItemCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.AgregarItemCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.ItemCarritoDTO;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CarritoDTO;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CheckoutResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito.CrearCarritoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Carrito;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.ItemCarrito;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;
import com.uade.TPO_Ecommerce_Grupo6.repository.CarritoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.ItemCarritoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.ProductoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;

    public CarritoService(CarritoRepository carritoRepository, ItemCarritoRepository itemCarritoRepository,
                          ProductoRepository productoRepository, UsuarioRepository usuarioRepository,
                          ProductoService productoService) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoService = productoService;
    }

    @Transactional
    public CarritoDTO crearCarrito(CrearCarritoRequest request) {
        if (carritoRepository.existsByUsuario_Id(request.getUsuarioId())) {
            throw new CarritoYaExisteException(request.getUsuarioId());
        }
        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.getUsuarioId()));
        Carrito carrito = Carrito.builder()
                .usuario(usuario)
                .build();
        carrito = carritoRepository.save(carrito);
        return convertirDTO(carrito);
    }

    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarrito(Long usuarioId) {
        Carrito carrito = buscarCarrito(usuarioId);
        return convertirDTO(carrito);
    }

    @Transactional
    public CarritoDTO agregarItem(AgregarItemCarritoRequest request) {
        Carrito carrito = buscarCarrito(request.getUsuarioId());
        Producto producto = productoRepository
                .findById(request.getProductoId())
                .orElseThrow(() -> new ProductoNoEncontradoException(request.getProductoId()));

        ItemCarrito itemExistente = itemCarritoRepository.findByCarrito_IdAndProducto_Id(carrito.getId(), producto.getId())
                .orElse(null);

        if (itemExistente != null) {
            int nuevaCantidad = itemExistente.getCantidad() + request.getCantidad();
            validarStock(producto, nuevaCantidad);
            itemExistente.setCantidad(nuevaCantidad);
            itemCarritoRepository.save(itemExistente);
        } else {
            validarStock(producto, request.getCantidad());
            ItemCarrito nuevoItem = ItemCarrito.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(request.getCantidad())
                    .build();
            carrito.agregarItem(nuevoItem);
            carritoRepository.save(carrito);
        }
        return convertirDTO(buscarCarrito(request.getUsuarioId()));
    }

    @Transactional
    public CarritoDTO actualizarCantidad(Long productoId, ActualizarItemCarritoRequest request) {
        Carrito carrito = buscarCarrito(request.getUsuarioId());
        ItemCarrito item = itemCarritoRepository.findByCarrito_IdAndProducto_Id(carrito.getId(), productoId)
                .orElseThrow(() -> new ItemCarritoNoEncontradoException(productoId));
        validarStock(item.getProducto(), request.getCantidad());
        item.setCantidad(request.getCantidad());
        itemCarritoRepository.save(item);
        return convertirDTO(buscarCarrito(request.getUsuarioId())
        );
    }

    @Transactional
    public CarritoDTO eliminarItem(Long usuarioId, Long productoId) {
        Carrito carrito = buscarCarrito(usuarioId);
        ItemCarrito item = itemCarritoRepository.findByCarrito_IdAndProducto_Id(carrito.getId(), productoId)
                .orElseThrow(() -> new ItemCarritoNoEncontradoException(productoId));
        carrito.eliminarItem(item);
        carritoRepository.save(carrito);
        return convertirDTO(buscarCarrito(usuarioId));
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = buscarCarrito(usuarioId);
        carrito.vaciar();
        carritoRepository.save(carrito);
    }

    @Transactional
    public CheckoutResponse checkout(Long usuarioId) {
        Carrito carrito = buscarCarrito(usuarioId);

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede realizar el checkout porque el carrito está vacío");
        }

        // 1. Reutilización de código: validar stock disponible para todos los ítems antes de proceder
        for (ItemCarrito item : carrito.getItems()) {
            validarStock(item.getProducto(), item.getCantidad());
        }

        // 2. Calcular monto total (precio * cantidad) y cantidad total de items
        BigDecimal montoTotal = BigDecimal.ZERO;
        int totalItemsComprados = 0;

        for (ItemCarrito item : carrito.getItems()) {
            BigDecimal subtotal = item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            montoTotal = montoTotal.add(subtotal);
            totalItemsComprados += item.getCantidad();
        }

        // 3. Descontar stock delegando en ProductoService dentro de la misma transacción
        for (ItemCarrito item : carrito.getItems()) {
            productoService.descontarStock(item.getProducto().getId(), item.getCantidad());
        }

        // 4. Vaciar el carrito tras checkout exitoso
        carrito.vaciar();
        carritoRepository.save(carrito);

        return CheckoutResponse.builder()
                .usuarioId(usuarioId)
                .cantidadItemsComprados(totalItemsComprados)
                .montoTotal(montoTotal)
                .fechaCheckout(LocalDateTime.now())
                .mensaje("Checkout realizado con éxito")
                .build();
    }

    private Carrito buscarCarrito(Long usuarioId) {
        return carritoRepository
                .findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new CarritoNoEncontradoException(usuarioId));
    }

    private void validarStock(Producto producto, Integer cantidadSolicitada) {
        if (cantidadSolicitada > producto.getStock()) {
            throw new StockInsuficienteException(
                    producto.getNombre(),
                    cantidadSolicitada,
                    producto.getStock()
            );
        }
    }

    private ItemCarritoDTO convertirItemDTO(ItemCarrito item) {
        Producto producto = item.getProducto();
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
        return ItemCarritoDTO.builder()
                .productoId(producto.getId())
                .nombre(producto.getNombre())
                .cantidad(item.getCantidad())
                .precioUnitario(producto.getPrecio())
                .subtotal(subtotal)
                .build();
    }

    private CarritoDTO convertirDTO(Carrito carrito) {
        List<ItemCarritoDTO> items = carrito.getItems()
                        .stream()
                        .map(this::convertirItemDTO)
                        .toList();
        BigDecimal total = items.stream().map(ItemCarritoDTO::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        int cantidadProductos = items.stream().mapToInt(ItemCarritoDTO::getCantidad).sum();
        return CarritoDTO.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuario().getId())
                .items(items)
                .cantidadProductos(cantidadProductos)
                .total(total)
                .build();
    }
}
