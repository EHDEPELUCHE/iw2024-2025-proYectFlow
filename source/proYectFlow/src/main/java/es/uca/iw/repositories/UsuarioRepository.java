package es.uca.iw.repositories;

import es.uca.iw.data.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Page<Usuario> findAll(Specification<Usuario> filter, Pageable pageable);

    Optional<Usuario> findByUsername(String nombre);
}
