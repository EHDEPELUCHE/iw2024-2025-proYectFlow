package es.uca.iw.proYectFlow;

import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.global.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = es.uca.iw.Application.class)
class ProYectFlowApplicationTests {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ProYectFlowApplicationTests proYectFlowApplicationTests;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(proYectFlowApplicationTests);
    }

    @Test
    void contextLoads() {
        Usuario usuarioAdmin = new Usuario("Administrador", "Administrador", "Administrador", "admin@flow.com", "Administrador", Roles.ADMIN);
        usuarioAdmin.setActivo(true);
        usuarioService.registerUserAdmin(usuarioAdmin);


        // Assuming contextLoads method does not take parameters
        // Assuming contextLoads method does not take parameters
        // proYectFlowApplicationTests.contextLoads();
        verify(usuarioService, times(1)).registerUserAdmin(usuarioAdmin);
        assertTrue(usuarioAdmin.getActivo());
    }
}