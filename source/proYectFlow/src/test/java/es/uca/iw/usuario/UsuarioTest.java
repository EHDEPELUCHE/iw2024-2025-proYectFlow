package es.uca.iw.usuario;

import es.uca.iw.global.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("John", "john_doe", "Doe", "john.doe@example.com", "password123", Roles.ADMIN);
    }

    @Test
    void testUsuarioCreation() {
        assertNotNull(usuario);
        assertEquals("John", usuario.getNombre());
        assertEquals("john_doe", usuario.getUsername());
        assertEquals("Doe", usuario.getApellido());
        assertEquals("john.doe@example.com", usuario.getCorreo());
        assertEquals("password123", usuario.getContrasenna());
        assertEquals(Roles.ADMIN, usuario.getTipo());
        assertFalse(usuario.getActivo());
    }

    @Test
    void testSettersAndGetters() {
        usuario.setNombre("Jane");
        usuario.setUsername("jane_doe");
        usuario.setApellido("Doe");
        usuario.setCorreo("jane.doe@example.com");
        usuario.setContrasenna("newpassword123");
        usuario.setTipo(Roles.SOLICITANTE);
        usuario.setActivo(true);
        usuario.setCodigo("12345");

        assertEquals("Jane", usuario.getNombre());
        assertEquals("jane_doe", usuario.getUsername());
        assertEquals("Doe", usuario.getApellido());
        assertEquals("jane.doe@example.com", usuario.getCorreo());
        assertEquals("newpassword123", usuario.getContrasenna());
        assertEquals(Roles.SOLICITANTE, usuario.getTipo());
        assertTrue(usuario.getActivo());
        assertEquals("12345", usuario.getCodigo());
    }

    @Test
    void testEqualsAndHashCode() {
        Usuario usuario2 = new Usuario("John", "john_doe", "Doe", "john.doe@example.com", "password123", Roles.ADMIN);
        assertEquals(usuario, usuario);
        assertEquals(usuario.hashCode(), usuario.hashCode());

        usuario2.setUsername("different_username");
        assertNotEquals(usuario, usuario2);
        assertNotEquals(usuario.hashCode(), usuario2.hashCode());
    }

    @Test
    void testAuthorities() {
        assertEquals(1, usuario.getAuthorities().size());
        assertTrue(usuario.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testIsEnabled() {
        assertFalse(usuario.isEnabled());
        usuario.setActivo(true);
        assertTrue(usuario.isEnabled());
    }

    @Test
    void testPassword() {
        assertEquals("password123", usuario.getPassword());
        assertEquals("password123", usuario.getHashedPassword());
    }

}