package br.caixabackend.controller;

import br.caixabackend.dto.request.PagamentoFiadoRequest;
import br.caixabackend.dto.response.FiadoResumoResponse;
import br.caixabackend.dto.response.PagamentoFiadoResponse;
import br.caixabackend.service.FiadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fiado")
@RequiredArgsConstructor
public class FiadoController {

    private final FiadoService fiadoService;

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<FiadoResumoResponse>> listarPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(fiadoService.listarFiadosPorEvento(eventoId));
    }

    @GetMapping("/evento/{eventoId}/abertos")
    public ResponseEntity<List<FiadoResumoResponse>> listarAbertos(@PathVariable Long eventoId) {
        return ResponseEntity.ok(fiadoService.listarFiadosAbertos(eventoId));
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<FiadoResumoResponse> buscarPorVenda(@PathVariable Long vendaId) {
        return ResponseEntity.ok(fiadoService.buscarPorVenda(vendaId));
    }

    @PostMapping("/venda/{vendaId}/pagamento")
    public ResponseEntity<PagamentoFiadoResponse> registrarPagamento(
            @PathVariable Long vendaId,
            @Valid @RequestBody PagamentoFiadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fiadoService.registrarPagamento(vendaId, request));
    }
}
