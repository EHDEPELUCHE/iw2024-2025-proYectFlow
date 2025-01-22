package es.uca.iw.security;

import es.uca.iw.usuario.Usuario;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<Usuario> {

    private final AuthenticatedUser authenticatedUser;

    public SpringSecurityAuditorAware(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public Optional<Usuario> getCurrentAuditor() {
        return Optional.ofNullable(authenticatedUser.get().orElse(null));
    }
}