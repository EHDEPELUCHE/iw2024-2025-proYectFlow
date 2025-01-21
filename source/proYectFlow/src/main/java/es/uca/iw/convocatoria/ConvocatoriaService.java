package es.uca.iw.convocatoria;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Clase de servicio para gestionar entidades de Convocatoria.
 * Esta clase proporciona métodos para realizar operaciones CRUD y gestionar el estado activo de Convocatoria.
 * Utiliza ConvocatoriaRepository para el acceso a datos y está anotada con @Service y @Validated.
 * 
 * @author 
 * @version 1.0
 */
@Service
@Validated
public class ConvocatoriaService implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ConvocatoriaRepository repository;
    public ConvocatoriaService(@Valid ConvocatoriaRepository repository) {
        this.repository = repository;
    }

    @Cacheable("Convocatoria")
    public Convocatoria convocatoriaActual() {
        return repository.findByActiva(true);
    }

    @Transactional
    public void hacerVigente(@Valid Convocatoria convocatoriaActual) {
        if (convocatoriaActual.getFechaFinal().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
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
    public void guardar(@Valid Convocatoria convocatoria) {
        repository.save(convocatoria);
    }

    public Page<Convocatoria> list(@Valid Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Convocatoria> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Convocatoria findById(@Valid UUID id) {
        Optional<Convocatoria> convocatoriaOptional = repository.findById(id);
        return convocatoriaOptional.orElse(null);
    }
}