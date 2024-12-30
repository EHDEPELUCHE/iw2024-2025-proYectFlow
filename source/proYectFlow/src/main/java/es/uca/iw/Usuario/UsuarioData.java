package es.uca.iw.Usuario;

import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Convocatoria.ConvocatoriaRepository;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import es.uca.iw.global.Roles;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class UsuarioData {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository, ProyectoRepository proyectoRepository, ConvocatoriaRepository convocatoriaRepository, PasswordEncoder passwordEncoder, ConvocatoriaService convocatoriaService) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                List<Usuario> usuarios = List.of(
                        new Usuario("Juan", "juan", "Pérez", "juan.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.ADMIN),
                        new Usuario("Maria", "maria", "López", "maria.lopez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.PROMOTOR),
                        new Usuario("Carlos", "carlos", "García", "carlos.garcia@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.PROMOTOR),
                        new Usuario("Lucia", "lucia", "Pérez", "lucia.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.SOLICITANTE),
                        new Usuario("Pedro", "pedro", "Pérez", "pedro.prez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.SOLICITANTE),
                        new Usuario("Sara", "sara", "Santos", "sara.santos@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.CIO),
                        new Usuario("Pablo", "pablo", "Pérez", "pablo.perez@example.com",
                                passwordEncoder.encode("pppppP1@"), Roles.OTP)
                        );
                usuarios.forEach(usuario -> usuario.setActivo(true));
                usuarioRepository.saveAll(usuarios);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            if (convocatoriaRepository.count() == 0) {
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
                Usuario promotor = usuarioRepository.findByUsername("maria");
                Usuario solicitante = usuarioRepository.findByUsername("lucia");
                Convocatoria convocatoriaActiva = convocatoriaService.ConvocatoriaActual();

                if (promotor != null && solicitante != null && convocatoriaActiva != null) {
                    List<Proyecto> proyectos = List.of(
                            new Proyecto("ProyectoEjemplo1", "Descripción1", "Interesados1",
                                    "Alcance1", new BigDecimal("10000"), new BigDecimal("150000"),
                                    promotor, solicitante, null, null),
                            new Proyecto( "ProyectoEjemplo2", "Descripción2", "Interesados2",
                                    "Alcanc2", new BigDecimal("20000"), new BigDecimal("250000"),
                                    promotor, solicitante, sdf.parse("1/09/2025"), null ),
                            new Proyecto( "ProyectoEjemplo3", "Descripción3", "Interesados3",
                                    "Alcance3", new BigDecimal("30000"), new BigDecimal("350000"),
                                    promotor, solicitante, sdf.parse("5/08/2025"), null )
                    );
                    proyectos.forEach(proyecto -> proyecto.setConvocatoria(convocatoriaActiva));
                    proyectoRepository.saveAll(proyectos);
                }
            }
        };
    }
}
