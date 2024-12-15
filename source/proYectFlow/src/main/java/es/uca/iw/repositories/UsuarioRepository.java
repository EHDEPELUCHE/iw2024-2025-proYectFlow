package es.uca.iw.repositories;

import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Page<Usuario> findAll(Specification<Usuario> filter, Pageable pageable);

    Usuario findByUsername(String nombre);

    List<Usuario> findByTipo(Roles roles);

    Usuario findByCorreo(String value);

    Usuario findByNombre(String nombre);

    void deleteByTipo(Roles role);
}
