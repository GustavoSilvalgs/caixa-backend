package br.caixabackend.service;

import br.caixabackend.dto.response.CategoriaRankingResponse;
import br.caixabackend.dto.response.OperadorResumoResponse;
import br.caixabackend.dto.response.ProdutoRankingResponse;
import br.caixabackend.dto.response.RelatorioEventoResponse;
import br.caixabackend.entity.ItemVenda;
import br.caixabackend.entity.Venda;
import br.caixabackend.enums.StatusVenda;
import br.caixabackend.enums.TipoVenda;
import br.caixabackend.repository.ItemVendaRepository;
import br.caixabackend.repository.PagamentoFiadoRepository;
import br.caixabackend.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final PagamentoFiadoRepository pagamentoFiadoRepository;
    private final EventoService eventoService;

    public RelatorioEventoResponse gerarRelatorio(Long eventoId) {
        var evento = eventoService.findById(eventoId);

        List<Venda> vendas = vendaRepository.findAllByEventoId(eventoId)
                .stream()
                .filter(v -> v.getStatus() == StatusVenda.CONCLUIDA)
                .toList();

        List<ItemVenda> itens = itemVendaRepository.findAllByEventoId(eventoId);

        // Totais gerais
        int totalVendas = vendas.size();
        int totalNormais = (int) vendas.stream().filter(v -> v.getTipo() == TipoVenda.NORMAL).count();
        int totalFiado = (int) vendas.stream().filter(v -> v.getTipo() == TipoVenda.FIADO).count();

        BigDecimal receitaTotal = vendas.stream()
                .map(Venda::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Receita recebida = normais + pagamentos de fiado recebidos
        BigDecimal receitaFiadoRecebida = vendas.stream()
                .filter(v -> v.getTipo() == TipoVenda.FIADO)
                .map(v -> pagamentoFiadoRepository.sumValorByVendaId(v.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receitaNormais = vendas.stream()
                .filter(v -> v.getTipo() == TipoVenda.NORMAL)
                .map(Venda::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receitaRecebida = receitaNormais.add(receitaFiadoRecebida);
        BigDecimal receitaPendente = receitaTotal.subtract(receitaRecebida);

        // Lucro total
        BigDecimal lucroTotal = itens.stream()
                .map(i -> {
                    BigDecimal lucroUnitario = i.getPrecoUnitario()
                            .subtract(i.getProduto().getPrecoCusto());
                    return lucroUnitario.multiply(BigDecimal.valueOf(i.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Receita por forma de pagamento
        Map<String, BigDecimal> receitaPorFormaPagamento = vendas.stream()
                .filter(v -> v.getTipo() == TipoVenda.NORMAL && v.getFormaPagamento() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getFormaPagamento().name(),
                        Collectors.reducing(BigDecimal.ZERO, Venda::getTotal, BigDecimal::add)
                ));

        vendas.stream()
                .filter(v -> v.getTipo() == TipoVenda.FIADO && v.getFormaPagamento() != null)
                .forEach(v -> receitaPorFormaPagamento.merge(
                        v.getFormaPagamento().name(),
                        v.getTotal(),
                        BigDecimal::add
                ));

        return RelatorioEventoResponse.builder()
                .eventoId(evento.getId())
                .eventoNome(evento.getNome())
                .eventoData(evento.getData())
                .totalVendas(totalVendas)
                .totalVendasNormais(totalNormais)
                .totalVendaFiado(totalFiado)
                .receitaTotal(receitaTotal)
                .receitaPorFormaPagamento(receitaPorFormaPagamento)
                .receitaRecebida(receitaRecebida)
                .receitaPendenteFiado(receitaPendente)
                .lucroTotal(lucroTotal)
                .produtosMaisVendidos(rankingPorQuantidade(itens))
                .produtosMaisLucrativos(rankingPorLucro(itens))
                .categorias(rankingPorCategoria(itens))
                .resumoPorOperador(resumoPorOperador(vendas))
                .build();
    }

    private List<ProdutoRankingResponse> rankingPorQuantidade(List<ItemVenda> itens) {
        return itens.stream()
                .collect(Collectors.groupingBy(i -> i.getProduto().getId()))
                .values().stream()
                .map(grupo -> {
                    var produto = grupo.get(0).getProduto();
                    int qtd = grupo.stream().mapToInt(ItemVenda::getQuantidade).sum();
                    BigDecimal receita = grupo.stream()
                            .map(ItemVenda::getSubtotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lucro = grupo.stream()
                            .map(i -> i.getPrecoUnitario()
                                    .subtract(produto.getPrecoCusto())
                                    .multiply(BigDecimal.valueOf(i.getQuantidade())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return ProdutoRankingResponse.builder()
                            .produtoId(produto.getId())
                            .produtoNome(produto.getNome())
                            .categoriaNome(produto.getCategoria().getNome())
                            .quantidadeVendida(qtd)
                            .receitaGerada(receita)
                            .lucroGerado(lucro)
                            .build();
                })
                .sorted(Comparator.comparingInt(ProdutoRankingResponse::getQuantidadeVendida).reversed())
                .toList();
    }

    private List<ProdutoRankingResponse> rankingPorLucro(List<ItemVenda> itens) {
        return rankingPorQuantidade(itens).stream()
                .sorted(Comparator.comparing(ProdutoRankingResponse::getLucroGerado).reversed())
                .toList();
    }

    private List<CategoriaRankingResponse> rankingPorCategoria(List<ItemVenda> itens) {
        return itens.stream()
                .collect(Collectors.groupingBy(i -> i.getProduto().getCategoria().getId()))
                .values().stream()
                .map(grupo -> {
                    var categoria = grupo.get(0).getProduto().getCategoria();
                    int qtd = grupo.stream().mapToInt(ItemVenda::getQuantidade).sum();
                    BigDecimal receita = grupo.stream()
                            .map(ItemVenda::getSubtotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal lucro = grupo.stream()
                            .map(i -> i.getPrecoUnitario()
                                    .subtract(i.getProduto().getPrecoCusto())
                                    .multiply(BigDecimal.valueOf(i.getQuantidade())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return CategoriaRankingResponse.builder()
                            .categoriaId(categoria.getId())
                            .categoriaNome(categoria.getNome())
                            .quantidadeVendida(qtd)
                            .receitaGerada(receita)
                            .lucroGerado(lucro)
                            .build();
                })
                .sorted(Comparator.comparing(CategoriaRankingResponse::getReceitaGerada).reversed())
                .toList();
    }

    private List<OperadorResumoResponse> resumoPorOperador(List<Venda> vendas) {
        return vendas.stream()
                .collect(Collectors.groupingBy(v -> v.getOperador().getId()))
                .values().stream()
                .map(grupo -> {
                    var operador = grupo.get(0).getOperador();
                    BigDecimal total = grupo.stream()
                            .map(Venda::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return OperadorResumoResponse.builder()
                            .operadorId(operador.getId())
                            .operadorNome(operador.getNome())
                            .totalVendas(grupo.size())
                            .totalArrecadado(total)
                            .build();
                })
                .sorted(Comparator.comparing(OperadorResumoResponse::getTotalArrecadado).reversed())
                .toList();
    }
}
