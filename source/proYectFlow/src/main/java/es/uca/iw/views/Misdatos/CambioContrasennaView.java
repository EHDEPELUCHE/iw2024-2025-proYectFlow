package es.uca.iw.views.Misdatos;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.data.Usuario;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.services.UsuarioService;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@PageTitle("Cambio Contraseña")
@Route("Cambiocontraseña")
@PermitAll
public class CambioContrasennaView extends Composite<VerticalLayout> {

    private final BeanValidationBinder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);
    private final AuthenticatedUser authenticatedUser;
    UsuarioService uservice ;
    private final PasswordEncoder passwordEncoder;

    private PasswordField contrasenna2 = new PasswordField();
    private PasswordField password1 = new PasswordField();
    private PasswordField password2 = new PasswordField();

    public CambioContrasennaView(AuthenticatedUser authenticatedUser, UsuarioService uservice, PasswordEncoder passwordEncoder) {
        this.authenticatedUser = authenticatedUser;
        this.uservice = uservice;
        this.passwordEncoder = passwordEncoder;

        Optional<Usuario> user = authenticatedUser.get();
        Usuario usuario = user.get();

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
        layoutColumn2.setHeight("min-content");
        h1.setWidth("100%");

        formLayout2Col.setWidth("100%");
        contrasenna2.setLabel("Contraseña actual");
        contrasenna2.setWidth("min-content");
        password1.setLabel("Nueva contraseña");
        password1.setWidth("min-content");
        password2.setLabel("Vuelva a escribir su nueva contraseña");
        password2.setWidth("min-content");

        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");

        buttonPrimary.addClickListener(e -> onSaveButtonClick(usuario));

        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary.addClickShortcut(Key.ENTER);
        buttonSecondary.setWidth("min-content");

        getContent().add(layoutColumn2);
        layoutColumn2.add(h1, formLayout2Col, layoutRow);
        formLayout2Col.add(contrasenna2, password1, password2);
        layoutRow.add(buttonPrimary, buttonSecondary);

        //binder.bindInstanceFields(this);
        //binder.setBean(usuario);
    }

    public void onSaveButtonClick(Usuario usuario) {

        if (!password1.getValue().equals(password2.getValue())) {
            Notification.show("Las contraseñas no coinciden");
            return;
        }
        if (!passwordEncoder.matches(contrasenna2.getValue(), usuario.getPassword())) {
            Notification.show("Contraseña errónea");
            return;
        }

        String cambiarContrasenna = passwordEncoder.encode(password1.getValue());

        if (binder.validate().isOk()) {

            try {
                usuario.setContrasenna(cambiarContrasenna);
                uservice.update(usuario);
                Notification.show("Contraseña actualizada con éxito.");
                contrasenna2.clear();
                password1.clear();
                password2.clear();
            } catch (Exception ex) {
                Notification.show("Error al actualizar la contraseña: " + ex.getMessage());
            }
        } else {
            Notification.show("Por favor, verifique los datos de entrada");
        }
    }
}