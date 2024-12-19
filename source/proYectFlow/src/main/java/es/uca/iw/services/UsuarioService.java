package es.uca.iw.services;

import es.uca.iw.data.EmailSender;
import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import es.uca.iw.data.rest.Promotor;
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
    private final EmailService emailService;
    public UsuarioService(UsuarioRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
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
    @Transactional
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
        user.setCodigo(UUID.randomUUID().toString().substring(0, 5));
        try {
            repository.save(user);
            //Aun no tenemos emailService
            emailService.sendEmail(user.getCorreo(), "Registro exitoso", "Bienvenido a proYectFlow. Inicie sesión en nuestra web y propón sus proyectos para mejorar nuestra universidad." +
                    "\n Antes de empezar active su cuenta en la siguiente url: " + emailService.getServerUrl() + "ActivarUsuario e introduzca el siguiente código de activación para poder empezar: " + user.getCodigo() );
            //emailService.sendRegistrationEmail(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public boolean registerUserAdmin(Usuario user) {
        user.setContrasenna(passwordEncoder.encode(user.getPassword()));
        user.setTipo(user.getTipo());
        user.setCodigo(UUID.randomUUID().toString().substring(0, 5));
        try {
            repository.save(user);
            //Aun no tenemos emailService
            emailService.sendEmail(user.getCorreo(), "Registro exitoso", "Bienvenido a proYectFlow. Inicie sesión en nuestra web y propón sus proyectos para mejorar nuestra universidad." +
                    "\n Antes de empezar active su cuenta en la siguiente url: " + emailService.getServerUrl() + "ActivarUsuario e introduzca el siguiente código de activación para poder empezar: " + user.getCodigo() );
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
    public void createPromotor(Promotor promotor) {
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


    public boolean activateUser(String email, String registerCode) {

        Usuario user = repository.findByCorreo(email);

        if (user != null && user.getCodigo().equals(registerCode)) {
            user.setActivo(true);
            repository.save(user);
            return true;

        } else {
            return false;
        }

    }

    public String encriptar(String textoplano) {
        return passwordEncoder.encode(textoplano);
    }
}
