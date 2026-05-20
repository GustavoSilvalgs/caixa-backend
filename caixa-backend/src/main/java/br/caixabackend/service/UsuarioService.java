package br.caixabackend.service;

import br.caixabackend.dto.request.UsuarioRequest;
import br.caixabackend.dto.response.UsuarioResponse;
import br.caixabackend.entity.Usuario;
import br.caixabackend.exception.BadRequestException;
import br.caixabackend.exception.NotFoundException;
import br.caixabackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .perfil(request.getPerfil())
                .ativo(true)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = findById(id);

        if (!usuario.getEmail().equals(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email já cadastrado");
        }

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setPerfil(request.getPerfil());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    public void alterarStatus(Long id, boolean ativo) {
        Usuario usuario = findById(id);
        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }
}
