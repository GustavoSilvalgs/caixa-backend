package br.caixabackend.service;

import br.caixabackend.dto.request.LoginRequest;
import br.caixabackend.dto.response.LoginResponse;
import br.caixabackend.entity.Usuario;
import br.caixabackend.repository.UsuarioRepository;
import br.caixabackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(
                Map.of("perfil", usuario.getPerfil().name()),
                userDetails
        );

        return LoginResponse.builder()
                .token(token)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .build();
    }
}