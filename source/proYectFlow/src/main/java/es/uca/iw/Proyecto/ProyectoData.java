package es.uca.iw.Proyecto;

import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Component("proyectoData")
@DependsOn({"usuarioData", "convocatoriaData"})

public class ProyectoData {

    @Bean
    CommandLineRunner initProyectoData(UsuarioRepository usuarioRepository, ProyectoRepository proyectoRepository, ConvocatoriaService convocatoriaService) {
        return args -> {

            if (proyectoRepository.count() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
