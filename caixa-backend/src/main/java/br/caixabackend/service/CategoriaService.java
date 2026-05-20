package br.caixabackend.service;

import br.caixabackend.dto.request.CategoriaRequest;
import br.caixabackend.dto.response.CategoriaResponse;
import br.caixabackend.entity.Categoria;
import br.caixabackend.exception.BadRequestException;
import br.caixabackend.exception.NotFoundException;
import br.caixabackend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNome(request.getNome())) {
            throw new BadRequestException("Categoria já cadastrada");
        }

        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .ativo(true)
                .build();

        return toResponse(categoriaRepository.save(categoria));
    }

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = findById(id);

        if (!categoria.getNome().equals(request.getNome())
                && categoriaRepository.existsByNome(request.getNome())) {
            throw new BadRequestException("Categoria já cadastrada");
        }

        categoria.setNome(request.getNome());
        return toResponse(categoriaRepository.save(categoria));
    }

    public void alterarStatus(Long id, boolean ativo) {
        Categoria categoria = findById(id);
        categoria.setAtivo(ativo);
        categoriaRepository.save(categoria);
    }

    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .ativo(categoria.getAtivo())
                .build();
    }
}