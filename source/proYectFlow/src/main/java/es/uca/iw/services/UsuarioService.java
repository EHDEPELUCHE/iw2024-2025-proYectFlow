package es.uca.iw.services;

import es.uca.iw.data.Usuario;
import es.uca.iw.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }


    public Optional<Usuario> get(UUID id) {
        return repository.findById(id);
    }

    public Optional<Usuario> getnombre(String nombre) {
        return Optional.of(repository.findByUsername(nombre));
    }

    public Usuario update(Usuario entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Usuario> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Usuario> list(Pageable pageable, Specification<Usuario> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}
