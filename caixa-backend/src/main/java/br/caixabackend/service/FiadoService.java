package br.caixabackend.service;

import br.caixabackend.dto.request.PagamentoFiadoRequest;
import br.caixabackend.dto.response.FiadoResumoResponse;
import br.caixabackend.dto.response.PagamentoFiadoResponse;
import br.caixabackend.entity.PagamentoFiado;
import br.caixabackend.entity.Venda;
import br.caixabackend.enums.TipoVenda;
import br.caixabackend.exception.BadRequestException;
import br.caixabackend.exception.NotFoundException;
import br.caixabackend.repository.PagamentoFiadoRepository;
import br.caixabackend.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FiadoService {

    private final VendaRepository vendaRepository;
    private final PagamentoFiadoRepository pagamentoFiadoRepository;

    public List<FiadoResumoResponse> listarFiadosPorEvento(Long eventoId) {
        return agruparPorCliente(
                vendaRepository.findAllByEventoIdAndTipo(eventoId, TipoVenda.FIADO)
        );
    }

    public List<FiadoResumoResponse> listarFiadosAbertos(Long eventoId) {
        return agruparPorCliente(
                vendaRepository.findAllByEventoIdAndTipo(eventoId, TipoVenda.FIADO)
        ).stream()
                .filter(f -> !f.getQuitado())
                .toList();
    }

    private List<FiadoResumoResponse> agruparPorCliente(List<Venda> vendas) {
        return vendas.stream()
                .collect(Collectors.groupingBy(v -> v.getClienteFiado().trim().toLowerCase()))
                .values().stream()
                .map(grupo -> {
                    String clienteNome = grupo.get(0).getClienteFiado();

                    BigDecimal valorTotal = grupo.stream()
                            .map(Venda::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalPago = grupo.stream()
                            .map(v -> pagamentoFiadoRepository.sumValorByVendaId(v.getId()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalRestante = valorTotal.subtract(totalPago);

                    List<PagamentoFiadoResponse> pagamentos = grupo.stream()
                            .flatMap(v -> pagamentoFiadoRepository.findAllByVendaId(v.getId())
                                    .stream()
                                    .map(p -> PagamentoFiadoResponse.builder()
                                            .id(p.getId())
                                            .vendaId(v.getId())
                                            .clienteFiado(v.getClienteFiado())
                                            .valorVenda(v.getTotal())
                                            .valorPago(p.getValor())
                                            .valorRestante(totalRestante)
                                            .quitado(totalRestante.compareTo(BigDecimal.ZERO) <= 0)
                                            .pagoEm(p.getPagoEm())
                                            .observacao(p.getObservacao())
                                            .build()))
                            .toList();

                    return FiadoResumoResponse.builder()
                            .vendaId(grupo.get(0).getId())
                            .clienteFiado(clienteNome)
                            .valorTotal(valorTotal)
                            .totalPago(totalPago)
                            .totalRestante(totalRestante)
                            .quitado(totalRestante.compareTo(BigDecimal.ZERO) <= 0)
                            .criadoEm(grupo.get(0).getCriadoEm())
                            .pagamentos(pagamentos)
                            .build();
                })
                .sorted(Comparator.comparing(FiadoResumoResponse::getTotalRestante).reversed())
                .toList();
    }

    public FiadoResumoResponse buscarPorVenda(Long vendaId) {
        Venda venda = findVendaFiado(vendaId);
        return toResumo(venda);
    }

    @Transactional
    public PagamentoFiadoResponse registrarPagamentoPorCliente(
            Long eventoId, String cliente, PagamentoFiadoRequest request) {

        List<Venda> vendas = vendaRepository
                .findAllByEventoIdAndTipo(eventoId, TipoVenda.FIADO)
                .stream()
                .filter(v -> v.getClienteFiado().trim().equalsIgnoreCase(cliente.trim()))
                .toList();

        if (vendas.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado: " + cliente);
        }

        BigDecimal totalPago = vendas.stream()
                .map(v -> pagamentoFiadoRepository.sumValorByVendaId(v.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotal = vendas.stream()
                .map(Venda::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal restante = valorTotal.subtract(totalPago);

        if (restante.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Esta dívida já está quitada");
        }

        if (request.getValor().compareTo(restante) > 0) {
            throw new BadRequestException(
                    "Valor informado (R$ " + request.getValor() +
                            ") é maior que o restante (R$ " + restante + ")"
            );
        }

        // Registra o pagamento na primeira venda em aberto
        Venda vendaAberta = vendas.stream()
                .filter(v -> pagamentoFiadoRepository
                        .sumValorByVendaId(v.getId())
                        .compareTo(v.getTotal()) < 0)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Nenhuma venda em aberto"));

        PagamentoFiado pagamento = PagamentoFiado.builder()
                .venda(vendaAberta)
                .valor(request.getValor())
                .observacao(request.getObservacao())
                .build();

        PagamentoFiado salvo = pagamentoFiadoRepository.save(pagamento);
        BigDecimal novoTotalPago = totalPago.add(request.getValor());
        BigDecimal novoRestante = valorTotal.subtract(novoTotalPago);

        return PagamentoFiadoResponse.builder()
                .id(salvo.getId())
                .vendaId(vendaAberta.getId())
                .clienteFiado(cliente)
                .valorVenda(valorTotal)
                .valorPago(novoTotalPago)
                .valorRestante(novoRestante)
                .quitado(novoRestante.compareTo(BigDecimal.ZERO) <= 0)
                .pagoEm(salvo.getPagoEm())
                .observacao(salvo.getObservacao())
                .build();
    }

    private Venda findVendaFiado(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada"));

        if (venda.getTipo() != TipoVenda.FIADO) {
            throw new BadRequestException("Esta venda não é do tipo fiado");
        }

        return venda;
    }

    private FiadoResumoResponse toResumo(Venda venda) {
        BigDecimal totalPago = pagamentoFiadoRepository.sumValorByVendaId(venda.getId());
        BigDecimal restante = venda.getTotal().subtract(totalPago);

        List<PagamentoFiadoResponse> pagamentos = pagamentoFiadoRepository
                .findAllByVendaId(venda.getId())
                .stream()
                .map(p -> PagamentoFiadoResponse.builder()
                        .id(p.getId())
                        .vendaId(venda.getId())
                        .clienteFiado(venda.getClienteFiado())
                        .valorVenda(venda.getTotal())
                        .valorPago(p.getValor())
                        .valorRestante(restante)
                        .quitado(restante.compareTo(BigDecimal.ZERO) <= 0)
                        .pagoEm(p.getPagoEm())
                        .observacao(p.getObservacao())
                        .build())
                .toList();

        return FiadoResumoResponse.builder()
                .vendaId(venda.getId())
                .clienteFiado(venda.getClienteFiado())
                .valorTotal(venda.getTotal())
                .totalPago(totalPago)
                .totalRestante(restante)
                .quitado(restante.compareTo(BigDecimal.ZERO) <= 0)
                .criadoEm(venda.getCriadoEm())
                .pagamentos(pagamentos)
                .build();
    }
}
