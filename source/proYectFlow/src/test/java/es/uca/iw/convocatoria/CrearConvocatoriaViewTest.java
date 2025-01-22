package es.uca.iw.convocatoria;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Test
    void testCrearConvocatoriaSuccess() {
        BigDecimalField presupuestototal = crearConvocatoriaView.presupuestototal;
        DatePicker fechaInicio = crearConvocatoriaView.fechaInicio;
        DatePicker fechaLimite = crearConvocatoriaView.fechaLimite;
        DatePicker fechaFinal = crearConvocatoriaView.fechaFinal;
        IntegerField recHumanos = crearConvocatoriaView.recHumanosDisponibles;
        Button guardarButton = (Button) crearConvocatoriaView.getContent().getComponentAt(2);

        presupuestototal.setValue(BigDecimal.valueOf(10000));
        fechaInicio.setValue(LocalDate.now());
        fechaLimite.setValue(LocalDate.now().plusDays(10));
        fechaFinal.setValue(LocalDate.now().plusDays(20));
        recHumanos.setValue(800);

        guardarButton.click();

        verify(convocatoriaService, times(1)).guardar(any(Convocatoria.class));
        assertNull(presupuestototal.getValue());
        assertNull(fechaInicio.getValue());
        assertNull(fechaLimite.getValue());
        assertNull(fechaFinal.getValue());
    }

    @Test
    void testCrearConvocatoriaFailure() {
        BigDecimalField presupuestototal = crearConvocatoriaView.presupuestototal;
        DatePicker fechaInicio = crearConvocatoriaView.fechaInicio;
        DatePicker fechaLimite = crearConvocatoriaView.fechaLimite;
        DatePicker fechaFinal = crearConvocatoriaView.fechaFinal;
        Button guardarButton = (Button) crearConvocatoriaView.getContent().getComponentAt(2);

        presupuestototal.setValue(BigDecimal.valueOf(10000));
        fechaInicio.setValue(LocalDate.now());
        fechaLimite.setValue(LocalDate.now().minusDays(10)); // Invalid date
        fechaFinal.setValue(LocalDate.now().plusDays(20));

        guardarButton.click();

        verify(convocatoriaService, times(0)).guardar(any(Convocatoria.class));
    }

    @Test
    void testCrearConvocatoriaEmptyFields() {
        Button guardarButton = (Button) crearConvocatoriaView.getContent().getComponentAt(2);

        guardarButton.click();

        verify(convocatoriaService, times(0)).guardar(any(Convocatoria.class));
    }

   
}