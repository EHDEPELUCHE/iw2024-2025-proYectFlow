package es.uca.iw.Convocatoria;

import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional
    public void hacerVigente(Convocatoria convocatoriaActual) {
        if (convocatoriaActual.getFecha_final().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede activar la convocatoria porque su fecha final ya ha pasado.");
        }
        Convocatoria convocatoriaAnterior = repository.findByActiva(true);

        if(convocatoriaAnterior != null) {
            convocatoriaAnterior.setActiva(false);
            repository.save(convocatoriaAnterior);
        }

        convocatoriaActual.setActiva(true);
        repository.save(convocatoriaActual);
    }

    @Transactional
    public void guardar(Convocatoria convocatoria) {
        repository.save(convocatoria);
    }

    public Page<Convocatoria> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Convocatoria findById(UUID id) {
        Optional<Convocatoria> convocatoriaOptional = repository.findById(id);
        return convocatoriaOptional.orElse(null);
    }
}
