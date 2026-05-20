package br.caixabackend.controller;

import br.caixabackend.dto.request.VendaRequest;
import br.caixabackend.dto.response.VendaResponse;
import br.caixabackend.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponse> realizar(@Valid @RequestBody VendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.realizar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<VendaResponse>> listarPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(vendaService.listarPorEvento(eventoId));
    }

    @GetMapping("/evento/{eventoId}/fiados")
    public ResponseEntity<List<VendaResponse>> listarFiadosPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(vendaService.listarFiadosPorEvento(eventoId));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.cancelar(id));
    }
}
