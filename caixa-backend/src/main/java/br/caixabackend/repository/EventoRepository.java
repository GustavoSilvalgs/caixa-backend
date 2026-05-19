package br.caixabackend.repository;

import br.caixabackend.entity.Evento;
import br.caixabackend.enums.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findAllByOrderByDataDesc();
    Optional<Evento> findFirstByStatusOrderByAbertoEmDesc(StatusEvento status);
    boolean existsByStatus(StatusEvento status);
}
