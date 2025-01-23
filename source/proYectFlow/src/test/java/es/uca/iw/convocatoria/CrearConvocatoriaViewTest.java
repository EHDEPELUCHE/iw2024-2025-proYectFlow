package es.uca.iw.convocatoria;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CrearConvocatoriaViewTest {

    private ConvocatoriaService convocatoriaService;
    private CrearConvocatoriaView crearConvocatoriaView;

    @BeforeEach
    void setUp() {
        convocatoriaService = mock(ConvocatoriaService.class);
        VaadinSession.setCurrent(mock(VaadinSession.class));
        UI.setCurrent(new UI());
        crearConvocatoriaView = new CrearConvocatoriaView(convocatoriaService);
    }

    @Test
    void testCrearConvocatoriaViewComponents() {
        assertNotNull(crearConvocatoriaView.presupuestototal);
        assertNotNull(crearConvocatoriaView.fechaInicio);
        assertNotNull(crearConvocatoriaView.fechaLimite);
        assertNotNull(crearConvocatoriaView.fechaFinal);
    }
}