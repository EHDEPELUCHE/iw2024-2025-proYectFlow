package es.uca.iw.proyecto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.UUID;


/**
 * Servicio para gestionar las entidades de AlineamientoEstrategico.
 * Proporciona métodos para recuperar, guardar y gestionar las entidades.
 */
@Service
@Validated
public class AlineamientoEstrategicoService {
    private final AlineamientoEstrategicoRepository repository;

    /**
     * Construye un nuevo AlineamientoEstrategicoService con el repositorio dado.
     *
     * @param repository el repositorio que se utilizará para el acceso a datos
     */
    public AlineamientoEstrategicoService(@Valid AlineamientoEstrategicoRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera todas las entidades de AlineamientoEstrategico.
     *
     * @return una lista de todas las entidades de AlineamientoEstrategico
     */
    public List<AlineamientoEstrategico> findAll() {
        return repository.findAll();
    }

    /**
     * Recupera todas las entidades activas de AlineamientoEstrategico.
     *
     * @return una lista de todas las entidades activas de AlineamientoEstrategico
     */
    public List<AlineamientoEstrategico> findAllActivos() {
        return repository.findByActivo(true);
    }

    /**
     * Recupera una entidad de AlineamientoEstrategico por su ID.
     *
     * @param id el ID de la entidad de AlineamientoEstrategico
     * @return la entidad de AlineamientoEstrategico, o null si no se encuentra
     */
    public AlineamientoEstrategico findById(@Valid UUID id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Guarda la entidad de AlineamientoEstrategico dada.
     *
     * @param alineamiento la entidad de AlineamientoEstrategico a guardar
     */
    @Transactional
    public void guardar(@Valid AlineamientoEstrategico alineamiento) {
        repository.save(alineamiento);
    }
}
