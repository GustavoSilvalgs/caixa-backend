package br.caixabackend.config;


import br.caixabackend.entity.Usuario;
import br.caixabackend.enums.PerfilUsuario;
import br.caixabackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@caixa.com")) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .email("admin@caixa.com")
                    .senhaHash(passwordEncoder.encode("admin123"))
                    .perfil(PerfilUsuario.ADMIN)
                    .ativo(true)
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuário admin criado: admin@caixa.com / admin123");
        }
    }
}
