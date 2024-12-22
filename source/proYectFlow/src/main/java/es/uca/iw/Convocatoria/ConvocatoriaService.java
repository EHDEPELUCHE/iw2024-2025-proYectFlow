package es.uca.iw.Convocatoria;

import es.uca.iw.Usuario.UsuarioRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ConvocatoriaService {
    private final ConvocatoriaRepository repository;
    public ConvocatoriaService(ConvocatoriaRepository repository) {
        this.repository = repository;
    }

    @Cacheable("Convocatoria")
    public Convocatoria ConvocatoriaActual() {
        return repository.findByActiva(true);
    }

    public void hacerVigente(Convocatoria convocatoriaActual, Convocatoria convocatoriaAnterior) {
        if(convocatoriaAnterior != null) {
            convocatoriaActual.setActiva(false);
            repository.save(convocatoriaAnterior);
        }

        convocatoriaActual.setActiva(true);
        repository.save(convocatoriaActual);
    }
}
