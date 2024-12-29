package es.uca.iw.Usuario;

import es.uca.iw.email.EmailService;
import es.uca.iw.global.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.domain.Page;
import com.vaadin.hilla.mappedtypes.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UsuarioRepository repository;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser() {
        Usuario user = new Usuario();
        user.setCorreo("test@example.com");
        user.setContrasenna("password");

        when(repository.save(any(Usuario.class))).thenReturn(user);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        boolean result = usuarioService.registerUser(user);

        assertTrue(result);
        verify(repository, times(1)).save(any(Usuario.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void registerUserAdmin() {
        Usuario usuarioAdmin = new Usuario("Administrador", "Administrador", "Administrador", "admin@flow.com", "Administrador", Roles.ADMIN);

        usuarioAdmin.setContrasenna(passwordEncoder.encode(usuarioAdmin.getPassword()));
        usuarioAdmin.setTipo(Roles.ADMIN);
        usuarioAdmin.setActivo(true);

        when(repository.save(any(Usuario.class))).thenReturn(usuarioAdmin);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        boolean result = usuarioService.registerUserAdmin(usuarioAdmin);

        assertTrue(result);
        verify(repository, times(1)).save(any(Usuario.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void activateUser() {
        Usuario user = new Usuario();
        user.setCorreo("test@example.com");
        user.setCodigo("12345");

        when(repository.findByCorreo(anyString())).thenReturn(user);
        when(repository.save(any(Usuario.class))).thenReturn(user);

        boolean result = usuarioService.activateUser("test@example.com", "12345");

        assertTrue(result);
        assertTrue(user.isEnabled());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registerUser_DataIntegrityViolationException() {
        Usuario user = new Usuario();
        user.setCorreo("test@example.com");
        user.setContrasenna("password");

        when(repository.save(any(Usuario.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        boolean result = usuarioService.registerUser(user);

        assertFalse(result);
        verify(repository, times(1)).save(any(Usuario.class));
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void getUsuarioById() {
        Usuario user = new Usuario();
        UUID id = user.getId();

        when(repository.findById(id)).thenReturn(Optional.of(user));

        Optional<Usuario> result = usuarioService.get(id);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getUsuarioByNombre() {
        String nombre = "testUser";
        Usuario user = new Usuario();
        user.setUsername(nombre);

        when(repository.findByUsername(nombre)).thenReturn(user);

        Optional<Usuario> result = usuarioService.getnombre(nombre);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(repository, times(1)).findByUsername(nombre);
    }

    @Test
    void deleteUserById() {
        UUID id = UUID.randomUUID();

        doNothing().when(repository).deleteById(id);

        usuarioService.delete(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deleteUserByRole() {
        Roles role = Roles.SOLICITANTE;

        doNothing().when(repository).deleteByTipo(role);

        usuarioService.delete(role);

        verify(repository, times(1)).deleteByTipo(role);
    }

  
    @Test
    void countUsuarios() {
        long count = 10L;

        when(repository.count()).thenReturn(count);

        int result = usuarioService.count();

        assertEquals(count, result);
        verify(repository, times(1)).count();
    }

    @Test
    void authenticateUser() {
        String username = "testUser";
        String password = "password";
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setContrasenna(passwordEncoder.encode(password));

        when(repository.findByUsername(username)).thenReturn(user);

        boolean result = usuarioService.authenticate(username, password);

        assertTrue(result);
        verify(repository, times(1)).findByUsername(username);
    }

    @Test
    void authenticateUser_Failure() {
        String username = "testUser";
        String password = "wrongPassword";
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setContrasenna(passwordEncoder.encode("password"));

        when(repository.findByUsername(username)).thenReturn(user);

        boolean result = usuarioService.authenticate(username, password);

        assertFalse(result);
        verify(repository, times(1)).findByUsername(username);
    }
}