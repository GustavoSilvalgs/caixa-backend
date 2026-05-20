package br.caixabackend.controller;

import br.caixabackend.dto.response.RelatorioEventoResponse;
import br.caixabackend.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<RelatorioEventoResponse> gerarRelatorio(@PathVariable Long eventoId) {
        return ResponseEntity.ok(relatorioService.gerarRelatorio(eventoId));
    }
}
