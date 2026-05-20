package br.caixabackend.service;

import br.caixabackend.dto.request.EventoRequest;
import br.caixabackend.dto.response.EventoResponse;
import br.caixabackend.entity.Evento;
import br.caixabackend.entity.Usuario;
import br.caixabackend.enums.StatusEvento;
import br.caixabackend.exception.BadRequestException;
import br.caixabackend.exception.NotFoundException;
import br.caixabackend.repository.EventoRepository;
import br.caixabackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public EventoResponse criar(EventoRequest request) {
        if (eventoRepository.existsByStatus(StatusEvento.ABERTO)) {
            throw new BadRequestException("Já existe um evento aberto. Feche-o antes de criar um novo.");
        }

        Usuario admin = getUsuarioLogado();

        Evento evento = Evento.builder()
                .nome(request.getNome())
                .data(request.getData())
                .status(StatusEvento.ABERTO)
                .criadoPor(admin)
                .build();

        return toResponse(eventoRepository.save(evento));
    }

    public List<EventoResponse> listar() {
        return eventoRepository.findAllByOrderByDataDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventoResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public EventoResponse buscarAberto() {
        return eventoRepository.findFirstByStatusOrderByAbertoEmDesc(StatusEvento.ABERTO)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Nenhum evento aberto no momento"));
    }

    public EventoResponse fechar(Long id) {
        Evento evento = findById(id);

        if (evento.getStatus() == StatusEvento.FECHADO) {
            throw new BadRequestException("Evento já está fechado");
        }

        evento.setStatus(StatusEvento.FECHADO);
        evento.setFechadoEm(LocalDateTime.now());

        return toResponse(eventoRepository.save(evento));
    }

    public Evento findById(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));
    }

    public Evento getEventoAberto() {
        return eventoRepository.findFirstByStatusOrderByAbertoEmDesc(StatusEvento.ABERTO)
                .orElseThrow(() -> new BadRequestException("Nenhum evento aberto. Peça ao administrador para abrir um evento."));
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private EventoResponse toResponse(Evento evento) {
        return EventoResponse.builder()
                .id(evento.getId())
                .nome(evento.getNome())
                .data(evento.getData())
                .status(evento.getStatus())
                .criadoPorNome(evento.getCriadoPor().getNome())
                .abertoEm(evento.getAbertoEm())
                .fechadoEm(evento.getFechadoEm())
                .build();
    }
}
