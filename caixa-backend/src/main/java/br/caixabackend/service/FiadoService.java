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
import java.util.List;

@Service
@RequiredArgsConstructor
public class FiadoService {

    private final VendaRepository vendaRepository;
    private final PagamentoFiadoRepository pagamentoFiadoRepository;

    public List<FiadoResumoResponse> listarFiadosPorEvento(Long eventoId) {
        return vendaRepository.findAllByEventoIdAndTipo(eventoId, TipoVenda.FIADO)
                .stream()
                .map(this::toResumo)
                .toList();
    }

    public List<FiadoResumoResponse> listarFiadosAbertos(Long eventoId) {
        return vendaRepository.findAllByEventoIdAndTipo(eventoId, TipoVenda.FIADO)
                .stream()
                .map(this::toResumo)
                .filter(f -> !f.getQuitado())
                .toList();
    }

    public FiadoResumoResponse buscarPorVenda(Long vendaId) {
        Venda venda = findVendaFiado(vendaId);
        return toResumo(venda);
    }

    @Transactional
    public PagamentoFiadoResponse registrarPagamento(Long vendaId, PagamentoFiadoRequest request) {
        Venda venda = findVendaFiado(vendaId);

        BigDecimal totalPago = pagamentoFiadoRepository.sumValorByVendaId(vendaId);
        BigDecimal restante = venda.getTotal().subtract(totalPago);

        if (restante.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Esta dívida já está quitada");
        }

        if (request.getValor().compareTo(restante) > 0) {
            throw new BadRequestException(
                    "Valor informado (R$ " + request.getValor() +
                            ") é maior que o restante (R$ " + restante + ")"
            );
        }

        PagamentoFiado pagamento = PagamentoFiado.builder()
                .venda(venda)
                .valor(request.getValor())
                .observacao(request.getObservacao())
                .build();

        PagamentoFiado salvo = pagamentoFiadoRepository.save(pagamento);

        BigDecimal novoTotalPago = totalPago.add(request.getValor());
        BigDecimal novoRestante = venda.getTotal().subtract(novoTotalPago);

        return PagamentoFiadoResponse.builder()
                .id(salvo.getId())
                .vendaId(venda.getId())
                .clienteFiado(venda.getClienteFiado())
                .valorVenda(venda.getTotal())
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
