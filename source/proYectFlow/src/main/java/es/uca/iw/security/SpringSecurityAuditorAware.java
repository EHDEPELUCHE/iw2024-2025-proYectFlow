package es.uca.iw.security;

import org.springframework.data.domain.AuditorAware;
import java.util.Optional;
import es.uca.iw.usuario.Usuario;

public class SpringSecurityAuditorAware implements AuditorAware<Usuario> {
    private AuthenticatedUser authenticatedUser;
    public SpringSecurityAuditorAware(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }
   
    @Override
    public Optional<Usuario> getCurrentAuditor() {
       try{
              return Optional.ofNullable(authenticatedUser.get().orElse(null));
       }catch (Exception e){
              return Optional.empty();
       }
    }
}