package es.uca.iw.Usuario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import es.uca.iw.global.Roles;
import java.util.List;

@Component
public class UsuarioData {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                List<Usuario> usuarios = List.of(
                        new Usuario("Juan", "juan", "Pérez", "juan.perez@example.com", passwordEncoder.encode("pppppP1@"), Roles.ADMIN),
                        new Usuario("Maria", "maria", "López", "maria.lopez@example.com", passwordEncoder.encode("pppppP1@"), Roles.PROMOTOR),
                        new Usuario("Carlos", "carlos", "García", "carlos.garcia@example.com", passwordEncoder.encode("pppppP1@"), Roles.PROMOTOR),
                        new Usuario("Lucia", "lucia", "Martínez", "lucia.martinez@example.com", passwordEncoder.encode("pppppP1@"), Roles.SOLICITANTE),
                        new Usuario("Pedro", "pedro", "Fernández", "pedro.fernandez@example.com", passwordEncoder.encode("pppppP1@"), Roles.SOLICITANTE),
                        new Usuario("Cristina", "cristina", "Martínez", "cristina.martinez@example.com", passwordEncoder.encode("pppppP1@"), Roles.CIO),
                        new Usuario("Pablo", "pablo", "Pérez", "pablo.perez@example.com", passwordEncoder.encode("pppppP1@"), Roles.OTP)
                        );
                usuarios.forEach(usuario -> usuario.setActivo(true));
                usuarioRepository.saveAll(usuarios);
            }
        };
    }
}
