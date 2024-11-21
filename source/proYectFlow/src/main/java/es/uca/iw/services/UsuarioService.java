package es.uca.iw.services;

import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import es.uca.iw.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

    public boolean authenticate(String username, String password) {
        Usuario u = repository.findByUsername(username);
        if (u != null) {
            //ENCRIPTAR
            if (u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    //HACER
    public boolean registerUser(Usuario user) {
        user.setContrasenna(passwordEncoder.encode(user.getPassword()));
        user.setTipo(Roles.SOLICITANTE);
        try {
            repository.save(user);
            //Aun no tenemos emailService
            //emailService.sendRegistrationEmail(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
