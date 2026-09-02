package com.uade.TPO_Ecommerce_Grupo6.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.exception.CategoriaNoEncontradaException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.categoria.CategoriaRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.categoria.CategoriaResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Categoria;
import com.uade.TPO_Ecommerce_Grupo6.repository.CategoriaRepository;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        Categoria categoria = buscarEntidadPorId(id);
        return convertirAResponse(categoria);
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre"
            );
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        return convertirAResponse(categoriaGuardada);
    }

    public CategoriaResponse actualizar(
            Long id,
            CategoriaRequest request) {

        Categoria categoria = buscarEntidadPorId(id);

        boolean cambioElNombre =
                !categoria.getNombre().equalsIgnoreCase(request.getNombre());

        if (cambioElNombre
                && categoriaRepository.existsByNombreIgnoreCase(
                        request.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre"
            );
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        Categoria categoriaActualizada =
                categoriaRepository.save(categoria);

        return convertirAResponse(categoriaActualizada);
    }

    public void eliminar(Long id) {
        Categoria categoria = buscarEntidadPorId(id);

        if (!categoria.getProductos().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede eliminar una categoría que tiene productos"
            );
        }

        categoriaRepository.delete(categoria);
    }

    private Categoria buscarEntidadPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(
                        () -> new CategoriaNoEncontradaException(id)
                );
    }

    private CategoriaResponse convertirAResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}