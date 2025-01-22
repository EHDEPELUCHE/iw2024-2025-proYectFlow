package es.uca.iw;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioRepository;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.security.SpringSecurityAuditorAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.sql.DataSource;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@Theme(value = "proyectflow")
@EnableJpaRepositories
@EnableJpaAuditing
public class Application implements AppShellConfigurator {

    public Application(UsuarioService usuarioService, ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
    }
    
    /*
    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Bean
    public AuditorAware<Usuario> auditorProvider() {
        return new SpringSecurityAuditorAware(authenticatedUser);
    }
    */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
                                                                               SqlInitializationProperties properties,
                                                                               UsuarioRepository repository) {
        // This bean ensures the database is only initialized when empty
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                if (repository.count() == 0L) {
                    return super.initializeDatabase();
                }
                return false;
            }
        };
    }
}