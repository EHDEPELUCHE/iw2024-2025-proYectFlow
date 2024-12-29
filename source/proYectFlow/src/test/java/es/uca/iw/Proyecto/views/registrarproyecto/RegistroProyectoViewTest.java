package es.uca.iw.Proyecto.views.registrarproyecto;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.upload.Upload;

import com.vaadin.flow.data.binder.Binder;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Usuario.UsuarioService;
import es.uca.iw.global.Roles;
import es.uca.iw.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
class RegistroProyectoViewTest {

    private UsuarioService usuarioService;
    private AuthenticatedUser authenticatedUser;
    private ProyectoService proyectoService;
    private ConvocatoriaService convocatoriaService;
    private RegistroProyectoView registroProyectoView;

    @BeforeEach
    void setUp() throws ParseException {
        usuarioService = mock(UsuarioService.class);
        authenticatedUser = mock(AuthenticatedUser.class);
        proyectoService = mock(ProyectoService.class);
        convocatoriaService = mock(ConvocatoriaService.class);

        when(authenticatedUser.get()).thenReturn(Optional.of(new Usuario()));
        registroProyectoView = new RegistroProyectoView(usuarioService, authenticatedUser, proyectoService, convocatoriaService);
        registroProyectoView.emailField = new EmailField();
        registroProyectoView.nombre = new TextField();
        registroProyectoView.descripcion = new TextField();
        registroProyectoView.interesados = new TextField();
        registroProyectoView.alcance = new TextField();
        registroProyectoView.coste = new BigDecimalField();
        registroProyectoView.aportacionInicial = new BigDecimalField();
        registroProyectoView.promotor = new ComboBox<Usuario>();
        registroProyectoView.upload = new Upload();
    }
    
    @Test
    void onRegistroProyectoTest() {
        registroProyectoView.nombre.setValue("Test Project");
        registroProyectoView.descripcion.setValue("Test Description");
        registroProyectoView.interesados.setValue("Test Interested");
        registroProyectoView.alcance.setValue("Test Scope");
        registroProyectoView.coste.setValue(new BigDecimal("1000"));
        registroProyectoView.aportacionInicial.setValue(new BigDecimal("500"));

        Usuario mockPromotor = mock(Usuario.class);
        when(usuarioService.get(Roles.PROMOTOR)).thenReturn(List.of(mockPromotor));
        Usuario solicitante4 = new Usuario("solicitante4", "solicitante4", "solicitante4", "solicitante4@flow.com", "solicitante4", Roles.SOLICITANTE);
        usuarioService.registerUser(solicitante4);
        registroProyectoView.emailField.setValue("solicitante4@flow.com");
        registroProyectoView.promotor.setItems(usuarioService.get(Roles.PROMOTOR));
        registroProyectoView.promotor.setValue(usuarioService.get(Roles.PROMOTOR).get(0));


        MemoryBuffer buffer = new MemoryBuffer();
        registroProyectoView.upload.setReceiver(buffer);

        onRegistroProyecto();
    }

    @Test
    void onRegistroProyectoWithInvalidData() {
        registroProyectoView.emailField.setValue("");
        registroProyectoView.nombre.setValue("");
        registroProyectoView.descripcion.setValue("");
        registroProyectoView.interesados.setValue("");
        registroProyectoView.alcance.setValue("");
        registroProyectoView.coste.setValue(null);
        registroProyectoView.aportacionInicial.setValue(null);
        registroProyectoView.promotor.setValue(null);

        onRegistroProyecto();

        verify(proyectoService, times(1)).registerProyecto(any(Proyecto.class));
    }



    public void onRegistroProyecto() {
        Usuario solicitante4 = new Usuario("solicitante4", "solicitante4", "solicitante4", "solicitante4@flow.com", "solicitante4", Roles.SOLICITANTE);

        Optional<Usuario> este = Optional.of(solicitante4);
        if (este.isPresent()) {
            Blob pdfBlob = null;
            try {
                pdfBlob = new javax.sql.rowset.serial.SerialBlob(registroProyectoView.buffer.getInputStream().readAllBytes());
            } catch (Exception ex) {

            }

            java.sql.Date fechaSql = null;
            if (registroProyectoView.fechaLimite.getValue() != null) {
                fechaSql = java.sql.Date.valueOf(registroProyectoView.fechaLimite.getValue());
            }

            Binder<Proyecto> binder = new Binder<>();
            binder.setBean(new Proyecto(registroProyectoView.nombre.getValue(), registroProyectoView.descripcion.getValue(), registroProyectoView.interesados.getValue(),
                    registroProyectoView.alcance.getValue(), registroProyectoView.coste.getValue(), registroProyectoView.aportacionInicial.getValue(),
                    registroProyectoView.promotor.getValue(), usuarioService.getCorreo(registroProyectoView.emailField.getValue()), fechaSql, pdfBlob));



            if (binder.validate().isOk()) {
                if (proyectoService.registerProyecto(binder.getBean())) {
                    binder.setBean(new Proyecto());

                }
            } else {

            }
        }
    }
}