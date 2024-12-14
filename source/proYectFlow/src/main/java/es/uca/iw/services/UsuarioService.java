package es.uca.iw.services;

import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import es.uca.iw.data.rest.promotores;
import es.uca.iw.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public boolean authenticate(String username, String password) {
        Usuario u = repository.findByUsername(username);
        if (u != null) {

            if (passwordEncoder.matches(password, u.getPassword())) {
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

    public boolean registerUserAdmin(Usuario user) {
        user.setContrasenna(passwordEncoder.encode(user.getPassword()));
        user.setTipo(user.getTipo());
        try {
            repository.save(user);
            //Aun no tenemos emailService
            //emailService.sendRegistrationEmail(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public List<Usuario> get(Roles roles) {
        return repository.findByTipo(roles);
    }

    public Usuario getCorreo(String value) {
        return repository.findByCorreo(value);
    }
    public Usuario getNombrePropio(String nombre){ return repository.findByNombre(nombre);}

    public void createPromotor(promotores promotor) {
        Usuario u = new Usuario(promotor.getNombre(),
                promotor.getNombre(), promotor.getApellido(),
                promotor.getCorreo(), "CAMBIARPORFAVOR", Roles.PROMOTOR);
        registerUserAdmin(u);

    }
}
