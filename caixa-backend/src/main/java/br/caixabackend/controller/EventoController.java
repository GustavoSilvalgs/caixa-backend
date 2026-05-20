package br.caixabackend.controller;

import br.caixabackend.dto.request.EventoRequest;
import br.caixabackend.dto.response.EventoResponse;
import br.caixabackend.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listar() {
        return ResponseEntity.ok(eventoService.listar());
    }

    @GetMapping("/aberto")
    public ResponseEntity<EventoResponse> buscarAberto() {
        return ResponseEntity.ok(eventoService.buscarAberto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/fechar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.fechar(id));
    }
}
