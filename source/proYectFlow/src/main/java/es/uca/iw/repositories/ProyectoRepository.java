package es.uca.iw.repositories;

import es.uca.iw.data.Proyecto;
import es.uca.iw.data.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {
    List<Proyecto> findByNombre(String nombre);

    Page<Proyecto> findAll(Specification<Proyecto> filter, Pageable pageable);


}
