package es.uca.iw.Convocatoria;

import es.uca.iw.Usuario.Usuario;
import es.uca.iw.global.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, UUID> {

    Convocatoria findByActiva(Boolean activa);

    Optional<Convocatoria> findById(UUID id);

}
