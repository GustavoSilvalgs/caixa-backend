package br.caixabackend.service;

import br.caixabackend.dto.request.ItemVendaRequest;
import br.caixabackend.dto.request.VendaRequest;
import br.caixabackend.dto.response.ItemVendaResponse;
import br.caixabackend.dto.response.VendaResponse;
import br.caixabackend.entity.*;
import br.caixabackend.enums.StatusVenda;
import br.caixabackend.enums.TipoVenda;
import br.caixabackend.exception.BadRequestException;
import br.caixabackend.exception.NotFoundException;
import br.caixabackend.repository.PagamentoFiadoRepository;
import br.caixabackend.repository.UsuarioRepository;
import br.caixabackend.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final EventoService eventoService;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoFiadoRepository pagamentoFiadoRepository;

    @Transactional
    public VendaResponse realizar(VendaRequest request) {
        // Valida fiado
        if (request.getTipo() == TipoVenda.FIADO &&
                (request.getClienteFiado() == null || request.getClienteFiado().isBlank())) {
            throw new BadRequestException("Nome do cliente é obrigatório para vendas no fiado");
        }

        Evento evento = eventoService.getEventoAberto();
        Usuario operador = getUsuarioLogado();

        Venda venda = Venda.builder()
                .evento(evento)
                .operador(operador)
                .tipo(request.getTipo())
                .status(StatusVenda.CONCLUIDA)
                .clienteFiado(request.getClienteFiado())
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (ItemVendaRequest itemRequest : request.getItens()) {
            Produto produto = produtoService.findById(itemRequest.getProdutoId());

            // Valida estoque
            if (produto.getEstoqueAtual() < itemRequest.getQuantidade()) {
                throw new BadRequestException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + produto.getEstoqueAtual()
                );
            }

            BigDecimal subtotal = produto.getPrecoVenda()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantidade()));

            ItemVenda item = ItemVenda.builder()
                    .venda(venda)
                    .produto(produto)
                    .quantidade(itemRequest.getQuantidade())
                    .precoUnitario(produto.getPrecoVenda())
                    .subtotal(subtotal)
                    .build();

            venda.getItens().add(item);
            total = total.add(subtotal);

            // Decrementa estoque
            produto.setEstoqueAtual(produto.getEstoqueAtual() - itemRequest.getQuantidade());
            produtoService.salvar(produto);
        }

        venda.setTotal(total);
        return toResponse(vendaRepository.save(venda));
    }

    @Transactional
    public VendaResponse cancelar(Long id) {
        Venda venda = findById(id);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new BadRequestException("Venda já está cancelada");
        }

        // Devolve estoque
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
            produtoService.salvar(produto);
        }

        venda.setStatus(StatusVenda.CANCELADA);
        return toResponse(vendaRepository.save(venda));
    }

    public VendaResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public List<VendaResponse> listarPorEvento(Long eventoId) {
        return vendaRepository.findAllByEventoId(eventoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VendaResponse> listarFiadosPorEvento(Long eventoId) {
        return vendaRepository.findAllByEventoIdAndTipo(eventoId, TipoVenda.FIADO)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Venda findById(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada"));
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private VendaResponse toResponse(Venda venda) {
        List<ItemVendaResponse> itens = venda.getItens().stream()
                .map(item -> ItemVendaResponse.builder()
                        .id(item.getId())
                        .produtoId(item.getProduto().getId())
                        .produtoNome(item.getProduto().getNome())
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return VendaResponse.builder()
                .id(venda.getId())
                .eventoId(venda.getEvento().getId())
                .eventoNome(venda.getEvento().getNome())
                .operadorNome(venda.getOperador().getNome())
                .tipo(venda.getTipo())
                .status(venda.getStatus())
                .total(venda.getTotal())
                .clienteFiado(venda.getClienteFiado())
                .criadoEm(venda.getCriadoEm())
                .itens(itens)
                .build();
    }
}
