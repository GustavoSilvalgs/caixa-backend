package br.caixabackend.repository;

import br.caixabackend.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByAtivoTrue();
    List<Produto> findAllByCategoriaIdAndAtivoTrue(Long categoriaId);
    List<Produto> findAllByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
}