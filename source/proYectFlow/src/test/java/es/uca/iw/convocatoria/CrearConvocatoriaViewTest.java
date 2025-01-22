package es.uca.iw.convocatoria;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CrearConvocatoriaViewTest {

    private ConvocatoriaService convocatoriaService;
    private UsuarioService usuarioService;
    private ProyectoService proyectoService;
    private CrearConvocatoriaView crearConvocatoriaView;

    @BeforeEach
    void setUp() {
        convocatoriaService = mock(ConvocatoriaService.class);
        usuarioService = mock(UsuarioService.class);
        proyectoService = mock(ProyectoService.class);
        VaadinSession.setCurrent(mock(VaadinSession.class));
        UI.setCurrent(new UI());
        crearConvocatoriaView = new CrearConvocatoriaView(convocatoriaService, usuarioService, proyectoService);
    }

    @Test
    void testCrearConvocatoriaViewComponents() {
        assertNotNull(crearConvocatoriaView.presupuestototal);
        assertNotNull(crearConvocatoriaView.fechaInicio);
        assertNotNull(crearConvocatoriaView.fechaLimite);
        assertNotNull(crearConvocatoriaView.fechaFinal);
    }
}