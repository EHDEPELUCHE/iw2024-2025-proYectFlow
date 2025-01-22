package es.uca.iw.proyecto.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEvent;

import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;

class GradoAvanceViewTest {

    @Mock
    private ProyectoService proyectoService;

    @Mock
    private BeforeEvent beforeEvent;

    private GradoAvanceView gradoAvanceView;
    private ConvocatoriaService convocatoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gradoAvanceView = new GradoAvanceView(proyectoService, convocatoriaService);
    }

    @Test
    void testSetParameterWithValidUUID() {
        UI ui = new UI();
        UI.setCurrent(ui);
        Proyecto proyecto = new Proyecto("p", "pd", "pi", "pa", BigDecimal.valueOf(200), BigDecimal.valueOf(2),
                    new Usuario(), new Usuario(), new Date(), null);
        proyecto.setGradoAvance(50.0);
        proyecto.setFechaSolicitud();

        assertEquals(50.0, proyecto.getGradoAvance());
    }

    @Test
    void testSetParameterWithInvalidUUID() {
        gradoAvanceView.setParameter(beforeEvent, "invalid-uuid");

        assertTrue(gradoAvanceView.proyecto.isEmpty());
    }

    @Test
    void testSetParameterWithEmptyParameter() {
        gradoAvanceView.setParameter(beforeEvent, "");

        assertTrue(gradoAvanceView.proyecto.isEmpty());
    }

    @Test
    void testActualizarGradoAvance() {
        UUID uuid = UUID.randomUUID();
        Proyecto proyecto = new Proyecto();
        proyecto.setGradoAvance(50.0);
        proyecto.setFechaSolicitud();
        when(proyectoService.get(uuid)).thenReturn(Optional.of(proyecto));
        gradoAvanceView.setParameter(beforeEvent, uuid.toString());

        BigDecimalField avance = new BigDecimalField();
        avance.setValue(BigDecimal.valueOf(75.0));
        Button actualizar = new Button();
        actualizar.addClickListener(e -> {
            Double valorSeleccionado = avance.getValue().doubleValue();
            proyecto.setGradoAvance(valorSeleccionado);
            proyectoService.update(proyecto);
            Notification.show("Grado de avance actualizado correctamente", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().refreshCurrentRoute(true);
        });
        assertEquals(50.0, proyecto.getGradoAvance());
    }
  

}