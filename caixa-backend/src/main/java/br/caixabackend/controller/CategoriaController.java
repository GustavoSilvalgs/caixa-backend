package br.caixabackend.controller;

import br.caixabackend.dto.request.CategoriaRequest;
import br.caixabackend.dto.response.CategoriaResponse;
import br.caixabackend.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoriaResponse>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        categoriaService.alterarStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        categoriaService.alterarStatus(id, false);
        return ResponseEntity.noContent().build();
    }
}
