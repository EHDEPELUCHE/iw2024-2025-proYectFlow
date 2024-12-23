package es.uca.iw.Usuario.views.controlacceso;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.UsuarioService;

@PageTitle("Registro de usuario")
@Route("registro-usuario")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@AnonymousAllowed
public class RegistrodeusuarioView extends Composite<VerticalLayout> {
    private final BeanValidationBinder<Usuario> binder;
    TextField username = new TextField();
    TextField nombre = new TextField();
    TextField apellido = new TextField();
    EmailField correo = new EmailField();
    PasswordField contrasenna = new PasswordField();

    UsuarioService servicio;

    public RegistrodeusuarioView(UsuarioService usuarioService) {
        PasswordField passwordField2 = new PasswordField();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonPrimary = new Button();
        Button buttonSecondary = new Button();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();

        this.servicio = usuarioService;
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Registro de usuario");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        username.setLabel("usuario");
        nombre.setLabel("nombre");
        apellido.setLabel("apellidos");
        correo.setLabel("email");
        contrasenna.setLabel("contraseña");
        contrasenna.setWidth("min-content");
        passwordField2.setLabel("Repetir contraseña");
        passwordField2.setWidth("min-content");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("Guardar");
        buttonPrimary.addClickListener(e -> onRegisterButtonClick(passwordField2));
        buttonPrimary.addClickShortcut(Key.ENTER);

        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSecondary.setText("Cancelar");
        buttonSecondary.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(username);
        formLayout2Col.add(nombre);
        formLayout2Col.add(apellido);
        formLayout2Col.add(correo);
        //formLayout2Col.add(textField3);
        formLayout2Col.add(contrasenna);
        formLayout2Col.add(passwordField2);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonPrimary);
        layoutRow.add(buttonSecondary);

        binder = new BeanValidationBinder<>(Usuario.class);
        binder.bindInstanceFields(this);

        //CAMBIAR PRUEBAS CONTRASEÑA
        binder.forField(contrasenna)
                .asRequired("La contraseña es obligatoria")
                .withValidator(password -> password != null && password.length() >= 8,
                        "La contraseña debe tener al menos 8 caracteres")
                .withValidator(password -> password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$"),
                        "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
                .bind(Usuario::getPassword, Usuario::setContrasenna);
    }

    public void onRegisterButtonClick(PasswordField passwordField2) {
        H4 status = new H4();
        Usuario usuarioRegistro = new Usuario(nombre.getValue(),
                username.getValue(), apellido.getValue(),
                correo.getValue(), contrasenna.getValue());
        Notification.show(usuarioRegistro.getPassword());
        binder.setBean(usuarioRegistro);
        Notification.show(binder.getBean().toString());

        if (!contrasenna.getValue().equals(passwordField2.getValue())) {
            Notification.show("Las contraseñas no coinciden");
            return;
        }

        if (binder.validate().isOk()) {
            if (servicio.registerUser(binder.getBean())) {
                status.setText("Excelente. ¡Por favor mira tu bandeja de entrada de correo!");
                status.setVisible(true);
                binder.setBean(new Usuario());
                Notification.show("Usuario registrado con éxito");
                passwordField2.setValue("");
            } else {
                Notification.show("El nombre de usuario o el email ya está en uso");
            }
        } else {
            Notification.show("Por favor, verifique los datos de entrada");
        }
    }
}
