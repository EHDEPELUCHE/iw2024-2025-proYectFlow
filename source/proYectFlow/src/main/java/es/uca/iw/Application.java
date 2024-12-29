package es.uca.iw;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.UsuarioRepository;
import es.uca.iw.Usuario.UsuarioService;
import es.uca.iw.global.Roles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;

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
                   meterSolicitantesprueba();
                   meterproyectosPrueba();
                    return super.initializeDatabase();

                }
                return false;
            }
        };
    }

    public void meterSolicitantesprueba(){
        Usuario usuarioAdmin;
        usuarioAdmin = new Usuario("Administrador", "Administrador", "Administrador", "admin@flow.com", "Administrador", Roles.ADMIN);
        usuarioAdmin.setActivo(true);
        usuarioService.registerUserAdmin(usuarioAdmin);

        Usuario usuarioCIO;
        usuarioCIO = new Usuario("CIO", "CIO", "CIO", "cio@flow.com", "CIO", Roles.CIO);
        usuarioCIO.setActivo(true);
        usuarioService.registerUserAdmin(usuarioCIO);

        Usuario usuarioOTP;
        usuarioOTP = new Usuario("OTP", "OTP", "OTP", "otp@flow.com", "OTP", Roles.OTP);
        usuarioOTP.setActivo(true);
        usuarioService.registerUserAdmin(usuarioOTP);

        Usuario solicitante;
        solicitante = new Usuario("solicitante1", "solicitante1", "solicitante1", "solicitante1@flow.com", "solicitante1", Roles.SOLICITANTE);
        solicitante.setActivo(true);
        usuarioService.registerUser(solicitante);

        Usuario solicitante2;
        solicitante2 = new Usuario("solicitante2", "solicitante2", "solicitante2", "solicitante2@flow.com", "solicitante2", Roles.SOLICITANTE);
        solicitante2.setActivo(true);
        usuarioService.registerUser(solicitante2);

        Usuario solicitante3;
        solicitante3 = new Usuario("solicitante3", "solicitante3", "solicitante3", "solicitante3@flow.com", "solicitante3", Roles.SOLICITANTE);
        solicitante3.setActivo(true);
        usuarioService.registerUser(solicitante3);

        Usuario solicitante4;
        solicitante4 = new Usuario("solicitante4", "solicitante4", "solicitante4", "solicitante4@flow.com", "solicitante4", Roles.SOLICITANTE);
        solicitante4.setActivo(true);
        usuarioService.registerUser(solicitante4);

        Usuario solicitante5;
        solicitante5 = new Usuario("solicitante5", "solicitante5", "solicitante5", "solicitante5@flow.com", "solicitante5", Roles.SOLICITANTE);
        solicitante5.setActivo(true);
        usuarioService.registerUser(solicitante5);

    }

    public void meterproyectosPrueba(){
        Convocatoria convocatoria = new Convocatoria(BigDecimal.valueOf(2000000), new Date("11/1/2025"), new Date("11/11/2024") , new Date("5/5/2025"));
        convocatoriaService.hacerVigente(convocatoria);
        Proyecto proyecto;
        proyecto = new Proyecto("Proyecto 1", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante1@flow.com"),null, new Date("5/5/2024"), null);
        proyecto.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto);

        Proyecto proyecto2;
        proyecto2 = new Proyecto("Proyecto 2", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante1@flow.com"),null, new Date("5/5/2024"), null);
        proyecto2.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto2);

        Proyecto proyecto3;
        proyecto3 = new Proyecto("Proyecto 3", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante2@flow.com"),null, new Date("5/5/2024"), null);
        proyecto3.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto3);

        Proyecto proyecto4;
        proyecto4 = new Proyecto("Proyecto 4", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante2@flow.com"),null, new Date("5/5/2024"), null);
        proyecto4.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto4);

        Proyecto proyecto5;
        proyecto5 = new Proyecto("Proyecto 5", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante3@flow.com"),null, new Date("5/5/2024"), null);
        proyecto5.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto5);

        Proyecto proyecto6;
        proyecto6 = new Proyecto("Proyecto 6", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante3@flow.com"),null, new Date("5/5/2024"), null);
        proyecto6.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto6);

        Proyecto proyecto7;
        proyecto7 = new Proyecto("Proyecto 7", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante4@flow.com"),null, new Date("5/5/2024"), null);
        proyecto7.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto7);

        Proyecto proyecto8;
        proyecto8 = new Proyecto("Proyecto 8", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante4@flow.com"),null, new Date("5/5/2024"), null);
        proyectoService.registerProyecto(proyecto8);

        Proyecto proyecto9;
        proyecto9 = new Proyecto("Proyecto 9", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante5@flow.com"),null, new Date("5/5/2024"), null);
        proyecto9.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto9);

        Proyecto proyecto10;
        proyecto10 = new Proyecto("Proyecto 10", "Primer proyecto de prueba", "Interesados de la uca",
                "alcance del proyecto", BigDecimal.valueOf(10000), BigDecimal.valueOf(700),
                usuarioService.getCorreo("solicitante5@flow.com"),null, new Date("5/5/2024"), null);
        proyecto10.setConvocatoria(convocatoria);
        proyectoService.registerProyecto(proyecto10);


    }
}
