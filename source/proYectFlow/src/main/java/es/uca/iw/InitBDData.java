package es.uca.iw;

import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Convocatoria.ConvocatoriaRepository;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoRepository;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.UsuarioRepository;
import es.uca.iw.global.Roles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class InitBDData {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, ConvocatoriaRepository convocatoriaRepository, ProyectoRepository proyectoRepository, ConvocatoriaService convocatoriaService) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                List<Usuario> usuarios = List.of(
                        new Usuario("Juan", "juan1", "Pérez", "juan.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.ADMIN),
                        new Usuario("Maria", "maria1", "López", "maria.lopez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.PROMOTOR),
                        new Usuario("Carlos", "carlos1", "García", "carlos.garcia@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.PROMOTOR),
                        new Usuario("Lucia", "lucia1", "Pérez", "lucia.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.SOLICITANTE),
                        new Usuario("Pedro", "pedro1", "Pérez", "pedro.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.SOLICITANTE),
                        new Usuario("Sara", "sara1", "Santos", "sara.santos@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.CIO),
                        new Usuario("Pablo", "pablo1", "Pérez", "pablo.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.OTP)
                );
                usuarios.forEach(usuario -> usuario.setActivo(true));
                usuarioRepository.saveAll(usuarios);
            }

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

            if (proyectoRepository.count() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Usuario promotor = usuarioRepository.findByUsername("maria");
                Usuario solicitante = usuarioRepository.findByUsername("lucia");
                Convocatoria convocatoriaActiva = convocatoriaService.ConvocatoriaActual();

                if (promotor != null && solicitante != null && convocatoriaActiva != null) {
                    List<Proyecto> proyectos = List.of(
                            new Proyecto("ProyectoEjemplo1", "Descripción1", "Interesados1",
                                    "Alcance1", new BigDecimal("1000"), new BigDecimal("10000"),
                                    promotor, solicitante, sdf.parse("1/08/2025"), null),

                            new Proyecto( "ProyectoEjemplo2", "Descripción2", "Interesados2",
                                    "Alcance2", new BigDecimal("2000"), new BigDecimal("20000"),
                                    promotor, solicitante, sdf.parse("1/09/2025"), null ),

                            new Proyecto( "ProyectoEjemplo3", "Descripción3", "Interesados3",
                                    "Alcance3", new BigDecimal("3000"), new BigDecimal("30000"),
                                    promotor, solicitante, sdf.parse("5/08/2025"), null ),

                            new Proyecto("ProyectoEjemplo4", "Descripción4", "Interesados4",
                                    "Alcance4", new BigDecimal("4000"), new BigDecimal("40000"),
                                    promotor, solicitante, null, null),

                            new Proyecto( "ProyectoEjemplo5", "Descripción5", "Interesados5",
                                    "Alcance5", new BigDecimal("5000"), new BigDecimal("50000"),
                                    promotor, solicitante, sdf.parse("1/07/2025"), null ),

                            new Proyecto( "ProyectoEjemplo6", "Descripción6", "Interesados6",
                                    "Alcance6", new BigDecimal("6000"), new BigDecimal("60000"),
                                    promotor, solicitante, sdf.parse("3/04/2025"), null ),

                            new Proyecto("ProyectoEjemplo7", "Descripción7", "Interesados7",
                                    "Alcance7", new BigDecimal("7000"), new BigDecimal("70000"),
                                    promotor, solicitante, null, null),

                            new Proyecto( "ProyectoEjemplo8", "Descripción8", "Interesados8",
                                    "Alcance8", new BigDecimal("8000"), new BigDecimal("80000"),
                                    promotor, solicitante, sdf.parse("17/09/2025"), null ),

                            new Proyecto( "ProyectoEjemplo9", "Descripción9", "Interesados9",
                                    "Alcance9", new BigDecimal("9000"), new BigDecimal("90000"),
                                    promotor, solicitante, sdf.parse("25/08/2025"), null ),

                            new Proyecto("ProyectoEjemplo10", "Descripción10", "Interesados10",
                                    "Alcance10", new BigDecimal("0"), new BigDecimal("1000"),
                                    promotor, solicitante, null, null),

                            new Proyecto("ProyectoEjemplo11", "Descripción11", "Interesados11",
                                    "Alcance11", new BigDecimal("100"), new BigDecimal("1000"),
                                    promotor, solicitante, null, null),

                            new Proyecto( "ProyectoEjemplo12", "Descripción12", "Interesados12",
                                    "Alcance12", new BigDecimal("200"), new BigDecimal("2000"),
                                    promotor, solicitante, sdf.parse("19/05/2025"), null ),

                            new Proyecto( "ProyectoEjemplo13", "Descripción13", "Interesados13",
                                    "Alcance13", new BigDecimal("300"), new BigDecimal("3000"),
                                    promotor, solicitante, sdf.parse("26/08/2025"), null ),

                            new Proyecto("ProyectoEjemplo14", "Descripción14", "Interesados14",
                                    "Alcance14", new BigDecimal("400"), new BigDecimal("4000"),
                                    promotor, solicitante, null, null)
                    );

                    List<Proyecto.Estado> estados = List.of(
                            Proyecto.Estado.solicitado, Proyecto.Estado.solicitado,
                            Proyecto.Estado.avalado, Proyecto.Estado.avalado,
                            Proyecto.Estado.evaluadoTecnicamente, Proyecto.Estado.evaluadoTecnicamente,
                            Proyecto.Estado.evaluadoEstrategicamente, Proyecto.Estado.evaluadoEstrategicamente,
                            Proyecto.Estado.noenDesarrollo, Proyecto.Estado.noenDesarrollo,
                            Proyecto.Estado.enDesarrollo, Proyecto.Estado.enDesarrollo,
                            Proyecto.Estado.denegado, Proyecto.Estado.denegado
                    );

                    for (int i = 0; i < proyectos.size(); i++) {
                        Proyecto proyecto = proyectos.get(i);
                        proyecto.setEstado(estados.get(i));
                        proyecto.setConvocatoria(convocatoriaActiva);
                    }
                    proyectoRepository.saveAll(proyectos);
                }
            }
        };
    }
}
