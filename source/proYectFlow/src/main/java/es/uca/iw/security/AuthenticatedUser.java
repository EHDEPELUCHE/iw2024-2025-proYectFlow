package es.uca.iw.security;


import ch.qos.logback.core.net.SMTPAppenderBase;
import com.vaadin.flow.spring.security.AuthenticationContext;
import es.uca.iw.data.Usuario;
import es.uca.iw.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
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
        SMTPAppenderBase<Object> userDetail = null;
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsername(userDetail.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
