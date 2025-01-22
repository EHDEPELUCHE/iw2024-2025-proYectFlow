package es.uca.iw.usuario;

import es.uca.iw.email.EmailService;
import es.uca.iw.global.Roles;
import es.uca.iw.rest.Promotor;
import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class UsuarioService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioRepository repository;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Cacheable("Usuario")
    public Optional<Usuario> get(UUID id) {
        return repository.findById(id);
    }

    public List<Usuario> list() {
        return repository.findAll();
    }

    @Cacheable("Usuario")
    public Optional<Usuario> getnombre(String nombre) {
        return Optional.of(repository.findByUsername(nombre));
    }
    @CachePut(value = "Usuario", key = "#usuario.id")
    public void update(Usuario entity) {
        repository.save(entity);
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
        return u != null && passwordEncoder.matches(password, u.getPassword());
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param user El objeto Usuario que contiene la información del usuario a registrar.
     * @return true si el registro fue exitoso, false si ocurrió una violación de integridad de datos.
     * 
     * Este método realiza las siguientes acciones:
     * 1. Codifica la contraseña del usuario.
     * 2. Establece el rol del usuario como SOLICITANTE.
     * 3. Genera un código único de activación para el usuario.
     * 4. Guarda el usuario en el repositorio.
     * 5. Envía un correo electrónico de bienvenida con instrucciones para activar la cuenta.
     * 
     * @throws DataIntegrityViolationException si ocurre una violación de integridad de datos al guardar el usuario.
     */
    public boolean registerUser(Usuario user) {
        user.setContrasenna(passwordEncoder.encode(user.getPassword()));
        user.setTipo(Roles.SOLICITANTE);
        user.setCodigo(UUID.randomUUID().toString().substring(0, 5));
        try {
            repository.save(user);
            emailService.sendEmail(user.getCorreo(), "Registro exitoso", "Bienvenido a proYectFlow. Inicie sesión en nuestra web y propón sus proyectos para mejorar nuestra universidad." +
                    "\n Antes de empezar active su cuenta en la siguiente url: " + emailService.getServerUrl() + "ActivarUsuario e introduzca el siguiente código de activación para poder empezar: " + user.getCodigo());
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
            emailService.sendEmail(user.getCorreo(), "Registro exitoso", "Bienvenido a proYectFlow. Inicie sesión en nuestra web y propón sus proyectos para mejorar nuestra universidad." +
                    "\n Antes de empezar active su cuenta en la siguiente url: " + emailService.getServerUrl() + "ActivarUsuario e introduzca el siguiente código de activación para poder empezar: " + user.getCodigo());
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Cacheable("Usuarios")
    public List<Usuario> get(Roles roles) {
        return repository.findByTipo(roles);
    }

    @Cacheable("Usuario")
    public Usuario getCorreo(String value) {
        return repository.findByCorreo(value);
    }


    /**
     * Crea un nuevo promotor o actualiza un usuario existente para convertirlo en promotor.
     * 
     * @param promotor El objeto Promotor que contiene la información del promotor a crear o actualizar.
     * @throws RuntimeException Si ocurre un error durante la creación o actualización del promotor.
     */
    @Transactional
    public void createPromotor(Promotor promotor) {
        try {
            Usuario existingUser = repository.findByCorreo(promotor.getCorreo());
            if (existingUser != null) {
                existingUser.setTipo(Roles.PROMOTOR);
                existingUser.setActivo(true);
                repository.save(existingUser);
            } else {
                Usuario u = new Usuario(promotor.getNombre(), promotor.getNombre(), promotor.getApellido(),
                        promotor.getCorreo(), "CAMBIARPORFAVOR", Roles.PROMOTOR);
                u.setActivo(true);
                try {
                    registerUserAdmin(u);
                } catch (Exception e) {
                    repository.save(u);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating promotor", e);
        }
    }

    /**
     * Este método cambia el rol de todos los usuarios que actualmente tienen el rol de PROMOTOR
     * a SOLICITANTE. Para ello, busca todos los usuarios con el rol de PROMOTOR en el repositorio,
     * y para cada uno de ellos, actualiza su rol a SOLICITANTE y guarda los cambios en el repositorio.
     */
    public void destituyePromotores() {
        List<Usuario> usuarios = repository.findByTipo(Roles.PROMOTOR);
        for (Usuario u : usuarios) {
            u.setTipo(Roles.SOLICITANTE);
            repository.save(u);
        }
    }

    /**
     * Activa un usuario si el código de registro proporcionado coincide.
     *
     * @param email El correo electrónico del usuario a activar.
     * @param registerCode El código de registro que debe coincidir para activar el usuario.
     * @return true si el usuario fue activado exitosamente, false en caso contrario.
     */
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
