package es.uca.iw.proyecto;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad Proyecto.
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas sobre la entidad Proyecto.
 * Extiende JpaRepository para heredar métodos básicos de persistencia.
 */
public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {
    List<Proyecto> findByNombre(String nombre);

    Page<Proyecto> findAll(Specification<Proyecto> filter, Pageable pageable);
    List<Proyecto> findAll(Specification<Proyecto> filter);

    List<Proyecto> findBySolicitante(Usuario usuario);

    List<Proyecto> findByConvocatoria(Convocatoria convocatoriaVieja);
}
