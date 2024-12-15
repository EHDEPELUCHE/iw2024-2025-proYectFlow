package es.uca.iw.services;

import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import es.uca.iw.data.rest.promotores;
import es.uca.iw.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;

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
    @Transactional
    public void delete(Roles role) {
        repository.deleteByTipo(role);
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

    @Transactional
    public void createPromotor(promotores promotor) {
        try {
            Usuario existingUser = repository.findByCorreo(promotor.getCorreo());
            if (existingUser != null) {
                Usuario u = existingUser;
                u.setTipo(Roles.PROMOTOR);
                repository.save(u);
            } else {
                Usuario u = new Usuario(promotor.getNombre(),
                        promotor.getNombre(), promotor.getApellido(),
                        promotor.getCorreo(), "CAMBIARPORFAVOR", Roles.PROMOTOR);
                try {
                    registerUserAdmin(u);
                } catch (Exception e) {
                   // logger.severe("Error registering user admin: " + e.getMessage());
                    repository.save(u);
                }
            }
        } catch (Exception e) {
           // logger.severe("Error creating promotor: " + e.getMessage());
            throw new RuntimeException("Error creating promotor", e);
        }
    }

    public void destituyePromotores() {
        List<Usuario> usuarios = repository.findByTipo(Roles.PROMOTOR);
        for (Usuario u : usuarios) {
            u.setTipo(Roles.SOLICITANTE);
            repository.save(u);
        }
    }
}
