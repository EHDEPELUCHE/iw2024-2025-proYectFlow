package es.uca.iw.convocatoria;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, UUID> {

    Convocatoria findByActiva(Boolean activa);

    Optional<Convocatoria> findById(UUID id);


}
