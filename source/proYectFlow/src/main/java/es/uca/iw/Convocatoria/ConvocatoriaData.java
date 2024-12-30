package es.uca.iw.Convocatoria;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Component("convocatoriaData")
public class ConvocatoriaData {

    @Bean
    CommandLineRunner initConvocatoriaData(ConvocatoriaRepository convocatoriaRepository) {
        return args -> {
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
        };
    }
}
