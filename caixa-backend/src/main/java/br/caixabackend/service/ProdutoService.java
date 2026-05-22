package br.caixabackend.service;

import br.caixabackend.dto.request.ProdutoRequest;
import br.caixabackend.dto.response.ProdutoResponse;
import br.caixabackend.entity.Categoria;
import br.caixabackend.entity.Produto;
import br.caixabackend.exception.NotFoundException;
import br.caixabackend.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaService categoriaService;

    public ProdutoResponse criar(ProdutoRequest request) {
        Categoria categoria = categoriaService.findById(request.getCategoriaId());

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .categoria(categoria)
                .imagemUrl(request.getImagemUrl())
                .precoCusto(request.getPrecoCusto())
                .precoVenda(request.getPrecoVenda())
                .estoqueAtual(request.getEstoqueAtual())
                .estoqueMinimo(request.getEstoqueMinimo())
                .ativo(true)
                .build();

        return toResponse(produtoRepository.save(produto));
    }

    public List<ProdutoResponse> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProdutoResponse> listar() {
        return produtoRepository.findAllByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProdutoResponse> listarPorCategoria(Long categoriaId) {
        return produtoRepository.findAllByCategoriaIdAndAtivoTrue(categoriaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProdutoResponse> buscarPorNome(String nome) {
        return produtoRepository.findAllByNomeContainingIgnoreCaseAndAtivoTrue(nome)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProdutoResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = findById(id);
        Categoria categoria = categoriaService.findById(request.getCategoriaId());

        produto.setNome(request.getNome());
        produto.setCategoria(categoria);
        produto.setImagemUrl(request.getImagemUrl());
        produto.setPrecoCusto(request.getPrecoCusto());
        produto.setPrecoVenda(request.getPrecoVenda());
        produto.setEstoqueAtual(request.getEstoqueAtual());
        produto.setEstoqueMinimo(request.getEstoqueMinimo());

        return toResponse(produtoRepository.save(produto));
    }

    public void alterarStatus(Long id, boolean ativo) {
        Produto produto = findById(id);
        produto.setAtivo(ativo);
        produtoRepository.save(produto);
    }

    public Produto findById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    private ProdutoResponse toResponse(Produto produto) {
        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .categoriaId(produto.getCategoria().getId())
                .categoriaNome(produto.getCategoria().getNome())
                .imagemUrl(produto.getImagemUrl())
                .precoCusto(produto.getPrecoCusto())
                .precoVenda(produto.getPrecoVenda())
                .estoqueAtual(produto.getEstoqueAtual())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .estoqueBaixo(produto.getEstoqueAtual() <= produto.getEstoqueMinimo())
                .ativo(produto.getAtivo())
                .build();
    }
}
