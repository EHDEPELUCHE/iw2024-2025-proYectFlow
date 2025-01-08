package es.uca.iw;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaRepository;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.AlineamientoEstrategico;
import es.uca.iw.proyecto.AlineamientoEstrategicoRepository;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoRepository;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioRepository;
import es.uca.iw.global.Roles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class InitBDData {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                               ConvocatoriaRepository convocatoriaRepository, ProyectoRepository proyectoRepository,
                               ConvocatoriaService convocatoriaService, AlineamientoEstrategicoRepository alineamientoEstrategicoRepository) throws ParseException {
        return args -> {
            insertConvocatorias(convocatoriaRepository);
            insertAlineamientos(alineamientoEstrategicoRepository);
            
            waitUntilConvocatoriaIsReady(convocatoriaRepository);
            insertUsers(usuarioRepository, passwordEncoder);
            
            waitUntilUsuarioIsReady(usuarioRepository, convocatoriaRepository);
            insertProjects(usuarioRepository, proyectoRepository, convocatoriaService);
        };
    }
    
    private void waitUntilConvocatoriaIsReady(ConvocatoriaRepository convocatoriaRepository) {
        while (convocatoriaRepository.count() < 3) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void waitUntilUsuarioIsReady(UsuarioRepository usuarioRepository, ConvocatoriaRepository convocatoriaRepository) {
        while (usuarioRepository.count() < 7 || convocatoriaRepository.count() < 3) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void insertConvocatorias(ConvocatoriaRepository convocatoriaRepository) throws Exception {
        if (convocatoriaRepository.count() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                List<Convocatoria> convocatorias = List.of(
                        new Convocatoria(new BigDecimal("1000000"), sdf.parse("15/02/2025"),
                                sdf.parse("15/09/2024"), sdf.parse("15/06/2025")),
                        new Convocatoria(new BigDecimal("750000"), sdf.parse("15/02/2024"),
                                sdf.parse("15/09/2023"), sdf.parse("15/06/2024")),
                        new Convocatoria(new BigDecimal("500000"), sdf.parse("15/02/2023"),
                                sdf.parse("15/09/2022"), sdf.parse("15/06/2023"))
                );
                convocatorias.get(0).setActiva(true);
                convocatorias.get(1).setActiva(false);
                convocatorias.get(2).setActiva(false);
                convocatoriaRepository.saveAll(convocatorias);
            }     
    }

    private void insertUsers(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        if (usuarioRepository.count() == 0) {
                List<Usuario> usuarios = List.of(
                        new Usuario("Juan", "juan1", "Pérez", "juan.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.ADMIN),
                        new Usuario("Maria", "maria2", "López", "maria.lopez@example.com",
                                passwordEncoder.encode("pppppP2@"), Roles.PROMOTOR),
                        new Usuario("Carlos", "carlos3", "García", "carlos.garcia@example.com",
                                passwordEncoder.encode("pppppP3@"), Roles.PROMOTOR),
                        new Usuario("Lucia", "lucia4", "González", "lucia.gonzalez@example.com",
                                passwordEncoder.encode("pppppP4@"), Roles.SOLICITANTE),
                        new Usuario("Pedro", "pedro5", "Ramos", "pedro.ramos@example.com",
                                passwordEncoder.encode("pppppP5@"), Roles.SOLICITANTE),
                        new Usuario("Sara", "sara6", "Santos", "sara.santos@example.com",
                                passwordEncoder.encode("pppppP6@"), Roles.CIO),
                        new Usuario("Pablo", "pablo7", "Palacios", "pablo.palacios@example.com",
                                passwordEncoder.encode("pppppP7@"), Roles.OTP)
                );
                usuarios.forEach(usuario -> usuario.setActivo(true));
                usuarioRepository.saveAll(usuarios);
            }
    }

    private void insertProjects(UsuarioRepository usuarioRepository,
                                ProyectoRepository proyectoRepository, ConvocatoriaService convocatoriaService) throws ParseException {
        if (proyectoRepository.count() == 0) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Usuario promotor = usuarioRepository.findByUsername("maria2");
            Usuario solicitante = usuarioRepository.findByUsername("lucia4");
            Convocatoria convocatoriaActiva = convocatoriaService.convocatoriaActual();

            if (promotor != null && solicitante != null && convocatoriaActiva != null) {
                List<Proyecto> proyectos = List.of(
                        new Proyecto("ProyectoEjemplo1", "Descripción1", "Interesados1",
                                "Alcance1", new BigDecimal("10000"), new BigDecimal("1000"),
                                promotor, solicitante, sdf.parse("1/08/2025"), null),
                        new Proyecto("ProyectoEjemplo2", "Descripción2", "Interesados2",
                                "Alcance2", new BigDecimal("20000"), new BigDecimal("2000"),
                                promotor, solicitante, sdf.parse("1/09/2025"), null),
                        new Proyecto("ProyectoEjemplo3", "Descripción3", "Interesados3",
                                "Alcance3", new BigDecimal("30000"), new BigDecimal("3000"),
                                promotor, solicitante, sdf.parse("5/08/2025"), null),
                        new Proyecto("ProyectoEjemplo4", "Descripción4", "Interesados4",
                                "Alcance4", new BigDecimal("40000"), new BigDecimal("4000"),
                                promotor, solicitante, null, null),
                        new Proyecto("ProyectoEjemplo5", "Descripción5", "Interesados5",
                                "Alcance5", new BigDecimal("50000"), new BigDecimal("5000"),
                                promotor, solicitante, sdf.parse("1/07/2025"), null),
                        new Proyecto("ProyectoEjemplo6", "Descripción6", "Interesados6",
                                "Alcance6", new BigDecimal("60000"), new BigDecimal("6000"),
                                promotor, solicitante, sdf.parse("3/04/2025"), null),
                        new Proyecto("ProyectoEjemplo7", "Descripción7", "Interesados7",
                                "Alcance7", new BigDecimal("70000"), new BigDecimal("7000"),
                                promotor, solicitante, null, null),
                        new Proyecto("ProyectoEjemplo8", "Descripción8", "Interesados8",
                                "Alcance8", new BigDecimal("8000"), new BigDecimal("8000"),
                                promotor, solicitante, sdf.parse("17/09/2025"), null),
                        new Proyecto("ProyectoEjemplo9", "Descripción9", "Interesados9",
                                "Alcance9", new BigDecimal("90000"), new BigDecimal("9000"),
                                promotor, solicitante, sdf.parse("25/08/2025"), null),
                        new Proyecto("ProyectoEjemplo10", "Descripción10", "Interesados10",
                                "Alcance10", new BigDecimal("100"), new BigDecimal("0"),
                                promotor, solicitante, null, null),
                        new Proyecto("ProyectoEjemplo11", "Descripción11", "Interesados11",
                                "Alcance11", new BigDecimal("1000"), new BigDecimal("100"),
                                promotor, solicitante, null, null),
                        new Proyecto("ProyectoEjemplo12", "Descripción12", "Interesados12",
                                "Alcance12", new BigDecimal("2000"), new BigDecimal("200"),
                                promotor, solicitante, sdf.parse("19/05/2025"), null),
                        new Proyecto("ProyectoEjemplo13", "Descripción13", "Interesados13",
                                "Alcance13", new BigDecimal("3000"), new BigDecimal("300"),
                                promotor, solicitante, sdf.parse("26/08/2025"), null),
                        new Proyecto("ProyectoEjemplo14", "Descripción14", "Interesados14",
                                "Alcance14", new BigDecimal("4000"), new BigDecimal("400"),
                                promotor, solicitante, null, null)
                );

                List<Proyecto.Estado> estados = List.of(
                        Proyecto.Estado.SOLICITADO, Proyecto.Estado.SOLICITADO,
                        Proyecto.Estado.AVALADO, Proyecto.Estado.AVALADO,
                        Proyecto.Estado.EVALUADO_TECNICAMENTE, Proyecto.Estado.EVALUADO_TECNICAMENTE,
                        Proyecto.Estado.EVALUADO_ESTRATEGICAMENTE, Proyecto.Estado.EVALUADO_ESTRATEGICAMENTE,
                        Proyecto.Estado.NO_EN_DESARROLLO, Proyecto.Estado.NO_EN_DESARROLLO,
                        Proyecto.Estado.EN_DESARROLLO, Proyecto.Estado.EN_DESARROLLO,
                        Proyecto.Estado.DENEGADO, Proyecto.Estado.DENEGADO
                );

                for (int i = 0; i < proyectos.size(); i++) {
                    Proyecto proyecto = proyectos.get(i);
                    proyecto.setEstado(estados.get(i));
                    proyecto.setConvocatoria(convocatoriaActiva);
                }
                proyectoRepository.saveAll(proyectos);
            }
        }
    }

    private void insertAlineamientos(AlineamientoEstrategicoRepository alineamientoEstrategicoRepository) {
        if(alineamientoEstrategicoRepository.count() == 0) {
                List<AlineamientoEstrategico> alineamientos = List.of(
                        new AlineamientoEstrategico("El proyecto trata de resolver una necesidad importante para la universidad"),
                        new AlineamientoEstrategico("Genera valor compartido con la Comunidad Universitaria"),
                        new AlineamientoEstrategico("Refuerza la importancia del papel de la UCA en la sociedad"),
                        new AlineamientoEstrategico("Gran número de personas o unidades que se verán afectadas de forma positiva"),
                        new AlineamientoEstrategico("Ahorro y / o mejora de la calidad de la gestión que se obtendrán"),
                        new AlineamientoEstrategico("La memoria contiene la información completa para poder estimar los costes de desarrollo"),
                        new AlineamientoEstrategico("Se han diseñado unos indicadores de éxito claros y medibles y unas metas realistas que reflejan el éxito de la solución"),
                        new AlineamientoEstrategico("Contiene elementos (técnicos, organizativos, normativos, presupuestarios, ...) que pueden facilitar la realización del proyecto"),
                        new AlineamientoEstrategico("Aumenta significativamente nuestro posicionamiento en investigación y transfiere de forma relevante y útil nuestra investigación a nuestro tejido social y productivo"),
                        new AlineamientoEstrategico("Contribuye a que la transparencia sea un valor distintivo y relevante en la UCA")
                );
                alineamientoEstrategicoRepository.saveAll(alineamientos);
        }
    }
}