package br.caixabackend.repository;

import br.caixabackend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findAllByAtivoTrue();
    boolean existsByNome(String nome);
}