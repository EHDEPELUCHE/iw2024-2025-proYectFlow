package es.uca.iw;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.UsuarioRepository;
import es.uca.iw.Usuario.UsuarioService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@Theme(value = "proyectflow")
@EnableJpaRepositories
public class Application implements AppShellConfigurator {
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final ConvocatoriaService convocatoriaService;

    public Application(UsuarioService usuarioService, ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        this.usuarioService = usuarioService;
        this.proyectoService = proyectoService;
        this.convocatoriaService = convocatoriaService;
    }

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
                    meterUsuariosPrueba();
                    meterProyectosPrueba();
                    return super.initializeDatabase();
                }
                return false;
            }
        };
    }

    public void meterUsuariosPrueba() {
    }

    public void meterProyectosPrueba() {
    }
}
