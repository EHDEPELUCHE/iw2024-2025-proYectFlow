package es.uca.iw.usuario.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@PageTitle("Cambio Contraseña")
@Route("Cambiocontraseña")
@PermitAll
public class CambioContrasennaView extends Composite<VerticalLayout> {
    private static final Logger logger = Logger.getLogger(CambioContrasennaView.class.getName());
    private final BeanValidationBinder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);
    private final AuthenticatedUser authenticatedUser;
    private final PasswordEncoder passwordEncoder;
    private static final String MIN_CONTENT = "min-content";
    final UsuarioService uservice;
    private final PasswordField contrasenna2 = new PasswordField();
    private final PasswordField password1 = new PasswordField();
    private final PasswordField password2 = new PasswordField();
    private Usuario usuario = new Usuario();

    public CambioContrasennaView(AuthenticatedUser authenticatedUser, UsuarioService uservice, PasswordEncoder passwordEncoder) {
        this.authenticatedUser = authenticatedUser;
        this.uservice = uservice;
        this.passwordEncoder = passwordEncoder;

        Optional<Usuario> user = authenticatedUser.get();
        
        if(user.isPresent()) {
            usuario = user.get();
            binder.setBean(usuario);
            logger.info("Usuario autenticado: " + usuario.getUsername());
        } else {
            logger.warning("No se encontró un usuario autenticado.");
        }

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H1 h1 = new H1("Cambio de contraseña");
        FormLayout formLayout2Col = new FormLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();

        Button buttonPrimary = new Button("Guardar");
        Button buttonSecondary = new Button("Cancelar");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight(MIN_CONTENT);
        h1.setWidth("100%");

        formLayout2Col.setWidth("100%");
        contrasenna2.setLabel("Contraseña actual");
        contrasenna2.setWidth(MIN_CONTENT);
        password1.setLabel("Nueva contraseña");
        password1.setWidth(MIN_CONTENT);
        password2.setLabel("Vuelva a escribir su nueva contraseña");
        password2.setWidth(MIN_CONTENT);

        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");

        buttonPrimary.addClickListener(e -> onSaveButtonClick(usuario));
        Button cancelarcambio = new Button("Volver", event -> UI.getCurrent().navigate(MisDatosView.class));
        cancelarcambio.addClassName("buttonSecondary");

        buttonPrimary.setWidth(MIN_CONTENT);
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary.addClickShortcut(Key.ENTER);
        buttonSecondary.setWidth(MIN_CONTENT);

        getContent().add(layoutColumn2);
        layoutColumn2.add(h1, formLayout2Col, layoutRow);
        formLayout2Col.add(contrasenna2, password1, password2);
        layoutRow.add(buttonPrimary, cancelarcambio);
    }

    public void onSaveButtonClick(Usuario usuario) {
        logger.info("Intentando cambiar la contraseña para el usuario: " + usuario.getUsername());

        if (!password1.getValue().equals(password2.getValue())) {
            Notification.show("Las contraseñas no coinciden");
            logger.warning("Las nuevas contraseñas no coinciden.");
            return;
        }
        if (!passwordEncoder.matches(contrasenna2.getValue(), usuario.getPassword())) {
            Notification.show("Contraseña errónea");
            logger.warning("La contraseña actual no es correcta.");
            return;
        }

        String cambiarContrasenna = passwordEncoder.encode(password1.getValue());

        if (binder.validate().isOk()) {
            try {
                usuario.setContrasenna(cambiarContrasenna);
                uservice.update(usuario);
                Notification.show("Contraseña actualizada con éxito.");
                logger.info("Contraseña actualizada con éxito para el usuario: " + usuario.getUsername());
                contrasenna2.clear();
                password1.clear();
                password2.clear();
                authenticatedUser.logout();
            } catch (Exception ex) {
                Notification.show("Error al actualizar la contraseña: " + ex.getMessage());
                logger.log(Level.SEVERE, String.format("Error al actualizar la contraseña para el usuario: %s", usuario.getUsername()), ex);
            }
        } else {
            Notification.show("Por favor, verifique los datos de entrada");
            logger.warning("Validación del formulario fallida.");
        }
    }
}
