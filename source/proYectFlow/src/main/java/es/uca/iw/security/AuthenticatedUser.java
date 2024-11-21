package es.uca.iw.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import es.uca.iw.data.Usuario;
import es.uca.iw.repositories.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UsuarioRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UsuarioRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<Usuario> get() {
        return authenticationContext.getAuthenticatedUser(Usuario.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()).get());


    }

    public void logout() {
        authenticationContext.logout();
    }

}
